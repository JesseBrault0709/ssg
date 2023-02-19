package com.jessebrault.ssg

import com.jessebrault.ssg.renderer.RenderContext
import com.jessebrault.ssg.specialpage.SpecialPage
import com.jessebrault.ssg.task.Output
import com.jessebrault.ssg.task.OutputMeta
import com.jessebrault.ssg.task.OutputMetaMap
import com.jessebrault.ssg.text.FrontMatter
import com.jessebrault.ssg.text.Text
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.Marker
import org.slf4j.MarkerFactory

import static com.jessebrault.ssg.util.ExtensionsUtil.stripExtension

@TupleConstructor(includeFields = true)
@NullCheck
@EqualsAndHashCode(includeFields = true)
class SimpleStaticSiteGenerator implements StaticSiteGenerator {

    private static final Logger logger = LoggerFactory.getLogger(SimpleStaticSiteGenerator)
    private static final Marker enter = MarkerFactory.getMarker('ENTER')
    private static final Marker exit = MarkerFactory.getMarker('EXIT')

    @Override
    Tuple2<Collection<Diagnostic>, Collection<Output>> generate(Build build) {
        logger.trace(enter, 'build: {}', build)
        logger.info('processing build with name: {}', build.name)

        def config = build.config
        def siteSpec = build.siteSpec

        // Get all texts, templates, parts, and specialPages
        def texts = config.textProviders.collectMany { it.provide() }
        def templates = config.templatesProviders.collectMany { it.provide() }
        def parts = config.partsProviders.collectMany { it.provide() }
        def specialPages = config.specialPagesProviders.collectMany { it.provide() }

        logger.debug('\n\ttexts: {}\n\ttemplates: {}\n\tparts: {}\n\tspecialPages: {}', texts, templates, parts, specialPages)

        def globals = build.globals
        Collection<Diagnostic> diagnostics = []
        Collection<Output> generatedPages = []

        Map<Text, OutputMeta> textOutputTasks = texts.collectEntries {
            [(it): new OutputMeta(it.path, stripExtension(it.path) + '.html')]
        }
        Map<SpecialPage, OutputMeta> specialPageOutputTasks = specialPages.collectEntries {
            [(it): new OutputMeta(it.path, stripExtension(it.path) + '.html')]
        }

        def textOutputMetas = textOutputTasks.values() as ArrayList
        def specialPageOutputMetas = specialPageOutputTasks.values() as ArrayList
        def outputMetaMap = new OutputMetaMap([*textOutputMetas, *specialPageOutputMetas])

        // Generate pages from each text, but only those that have a 'template' frontMatter field with a valid value
        textOutputTasks.each { text, outputMeta ->
            logger.trace(enter, 'text: {}, outputMeta: {}', text, outputMeta)
            logger.info('processing text: {}', text.path)

            // Extract frontMatter from text
            def frontMatterResult = text.type.frontMatterGetter.get(text)
            FrontMatter frontMatter
            if (frontMatterResult.v1.size() > 0) {
                logger.debug('diagnostics for getting frontMatter for {}: {}', text.path, frontMatterResult.v1)
                diagnostics.addAll(frontMatterResult.v1)
                logger.trace(exit, '')
                return
            } else {
                frontMatter = frontMatterResult.v2
                logger.debug('frontMatter: {}', frontMatter)
            }

            // Find the appropriate template from the frontMatter
            def desiredTemplate = frontMatter.find('template')
            if (desiredTemplate.isEmpty()) {
                logger.info('{} has no \'template\' key in its frontMatter; skipping generation', text)
                return
            }
            def template = templates.find { it.path == desiredTemplate.get() }
            if (template == null) {
                diagnostics << new Diagnostic('in textFile' + text.path + ' frontMatter.template references an unknown template: ' + desiredTemplate, null)
                logger.trace(exit, '')
                return
            }
            logger.debug('found template: {}', template)

            // Render the template using the result of rendering the text earlier
            def templateRenderResult = template.type.renderer.render(
                    template,
                    text,
                    new RenderContext(
                            config,
                            siteSpec,
                            globals,
                            texts,
                            parts,
                            outputMeta.sourcePath,
                            outputMeta.targetPath
                    )
            )
            String renderedTemplate
            if (templateRenderResult.v1.size() > 0) {
                diagnostics.addAll(templateRenderResult.v1)
                logger.trace(exit, '')
                return
            } else {
                renderedTemplate = templateRenderResult.v2
            }

            // Create a GeneratedPage
            generatedPages << new Output(outputMeta, renderedTemplate)
            return
        }

        // Generate special pages
        specialPageOutputTasks.each { specialPage, outputMeta ->
            logger.info('processing specialPage: {}', specialPage.path)

            def specialPageRenderResult = specialPage.type.renderer.render(
                    specialPage,
                    new RenderContext(
                            config,
                            siteSpec,
                            globals,
                            texts,
                            parts,
                            outputMeta.sourcePath,
                            outputMeta.targetPath
                    )
            )
            String renderedSpecialPage
            if (specialPageRenderResult.v1.size() > 0) {
                diagnostics.addAll(specialPageRenderResult.v1)
                logger.trace(exit, '')
                return
            } else {
                renderedSpecialPage = specialPageRenderResult.v2
            }

            // Create a GeneratedPage
            generatedPages << new Output(outputMeta, renderedSpecialPage)
            return
        }

        logger.trace(exit, '\n\tdiagnostics: {}\n\tgeneratedPages: {}', diagnostics, generatedPages)
        new Tuple2<>(diagnostics, generatedPages)
    }

    @Override
    String toString() {
        "SimpleStaticSiteGenerator()"
    }

}
