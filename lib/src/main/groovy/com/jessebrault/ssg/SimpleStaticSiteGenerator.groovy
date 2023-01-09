package com.jessebrault.ssg

import com.jessebrault.ssg.text.FrontMatter
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.Marker
import org.slf4j.MarkerFactory

@TupleConstructor(includeFields = true)
@NullCheck
@EqualsAndHashCode(includeFields = true)
class SimpleStaticSiteGenerator implements StaticSiteGenerator {

    private static final Logger logger = LoggerFactory.getLogger(SimpleStaticSiteGenerator)
    private static final Marker enter = MarkerFactory.getMarker('ENTER')
    private static final Marker exit = MarkerFactory.getMarker('EXIT')

    @Override
    Tuple2<Collection<Diagnostic>, Collection<GeneratedPage>> generate(Build build) {
        logger.trace(enter, 'build: {}', build)

        def config = build.config

        // Get all texts, templates, parts, and specialPages
        def texts = config.textProviders.collectMany { it.getTextFiles() }
        def templates = config.templatesProviders.collectMany { it.getTemplates() }
        def parts = config.partsProviders.collectMany { it.getParts() }
        def specialPages = config.specialPagesProviders.collectMany { it.getSpecialPages() }

        logger.debug('\n\ttexts: {}\n\ttemplates: {}\n\tparts: {}\n\tspecialPages: {}', texts, templates, parts, specialPages)

        def globals = build.globals
        Collection<Diagnostic> diagnostics = []
        Collection<GeneratedPage> generatedPages = []

        // Generate pages from each text
        texts.each {
            logger.trace(enter, 'text: {}', it)
            logger.info('processing text: {}', it.path)

            // Render the text (i.e., transform text to html)
            def textRenderResult = it.type.renderer.render(it, globals)
            String renderedText
            if (textRenderResult.v1.size() > 0) {
                logger.debug('diagnostics for rendering {}: {}', it.path, textRenderResult.v1)
                diagnostics.addAll(textRenderResult.v1)
                logger.trace(exit, '')
                return
            } else {
                renderedText = textRenderResult.v2
                logger.debug('renderedText: {}', renderedText)
            }

            // Extract frontMatter from text
            def frontMatterResult = it.type.frontMatterGetter.get(it)
            FrontMatter frontMatter
            if (frontMatterResult.v1.size() > 0) {
                logger.debug('diagnostics for getting frontMatter for {}: {}', it.path, frontMatterResult.v1)
                diagnostics.addAll(frontMatterResult.v1)
                logger.trace(exit, '')
                return
            } else {
                frontMatter = frontMatterResult.v2
                logger.debug('frontMatter: {}', frontMatter)
            }

            // Find the appropriate template from the frontMatter
            def desiredTemplate = frontMatter['template']
            logger.debug('desiredTemplate name: {}', desiredTemplate)
            if (desiredTemplate == null || desiredTemplate.isEmpty() || desiredTemplate.isBlank()) {
                diagnostics << new Diagnostic('in textFile ' + it.path + ' frontMatter.template must not be empty, blank, or missing', null)
                logger.trace(exit, '')
                return
            }
            def template = templates.find { it.path == desiredTemplate }
            if (template == null) {
                diagnostics << new Diagnostic('in textFile' + it.path + ' frontMatter.template references an unknown template: ' + desiredTemplate, null)
                logger.trace(exit, '')
                return
            }
            logger.debug('found template: {}', template)

            // Render the template using the result of rendering the text earlier
            def templateRenderResult = template.type.renderer.render(template, frontMatter, renderedText, parts, globals)
            String renderedTemplate
            if (templateRenderResult.v1.size() > 0) {
                diagnostics.addAll(templateRenderResult.v1)
                logger.trace(exit, '')
                return
            } else {
                renderedTemplate = templateRenderResult.v2
            }

            // Create a GeneratedPage
            generatedPages << new GeneratedPage(it.path, renderedTemplate)
        }

        // Generate special pages
        specialPages.each {
            logger.info('processing specialPage: {}', it.path)

            def specialPageRenderResult = it.type.renderer.render(it, texts, parts, globals)
            String renderedSpecialPage
            if (specialPageRenderResult.v1.size() > 0) {
                diagnostics.addAll(specialPageRenderResult.v1)
                logger.trace(exit, '')
                return
            } else {
                renderedSpecialPage = specialPageRenderResult.v2
            }

            // Create a GeneratedPage
            generatedPages << new GeneratedPage(it.path, renderedSpecialPage)
        }

        logger.trace(exit, '\n\tdiagnostics: {}\n\tgeneratedPages: {}', diagnostics, generatedPages)
        new Tuple2<>(diagnostics, generatedPages)
    }

    @Override
    String toString() {
        "SimpleStaticSiteGenerator()"
    }

}
