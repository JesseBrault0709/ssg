package com.jessebrault.ssg

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

    private final Config config

    @Override
    void generate(File buildDir, Map globals) {
        logger.trace(enter, 'buildDir: {}, globals: {}', buildDir, globals)

        // Get all texts, templates, parts, and specialPages
        def texts = this.config.textProviders.collectMany { it.getTextFiles() }
        def templates = this.config.templatesProviders.collectMany { it.getTemplates() }
        def parts = this.config.partsProviders.collectMany { it.getParts() }
        def specialPages = this.config.specialPagesProviders.collectMany { it.getSpecialPages() }

        logger.debug('\n\ttexts: {}\n\ttemplates: {}\n\tparts: {}\n\tspecialPages: {}', texts, templates, parts, specialPages)

        // Define output function
        def outputPage = { String path, String result ->
            def outFile = new File(buildDir, path + '.html')
            if (outFile.exists()) {
                logger.info('outFile {} already exists, deleting', outFile)
                outFile.delete()
            }
            outFile.createParentDirectories()
            logger.info('writing result to {}', outFile)
            outFile.write(result)
        }

        // Generate pages from each text
        texts.each {
            logger.info('processing text: {}', it.path)

            // Render the text (i.e., transform text to html)
            def renderedText = it.type.renderer.render(it.text, globals)
            logger.debug('renderedText: {}', renderedText)

            // Extract frontMatter from text
            def frontMatter = it.type.frontMatterGetter.get(it.text)
            logger.debug('frontMatter: {}', frontMatter)

            // Find the appropriate template from the frontMatter
            def desiredTemplate = frontMatter['template']
            logger.debug('desiredTemplate name: {}', desiredTemplate)
            if (desiredTemplate == null) {
                throw new IllegalArgumentException('in text ' + it.path + ' frontMatter.template must not be null')
            }
            if (desiredTemplate.isEmpty() || desiredTemplate.isBlank()) {
                throw new IllegalArgumentException('in text ' + it.path + ' frontMatter.template must not be empty, blank, or missing')
            }
            def template = templates.find {
                it.relativePath == desiredTemplate
            }
            logger.debug('template: {}', template)
            if (template == null) {
                throw new IllegalArgumentException('in textFile' + it.path + ' unknown template: ' + desiredTemplate)
            }

            // Render the template using the result of rendering the text earlier
            def result = template.type.renderer.render(template, frontMatter, renderedText, parts, globals)
            logger.debug('result: {}', result)

            // Output the result to the outfile, an .html file
            outputPage(it.path, result)
        }

        // Generate special pages
        specialPages.each {
            logger.info('processing specialPage: {}', it)

            def result = it.type.renderer.render(it.text, texts, parts, globals)
            logger.info('result: {}', result)

            // Output result to file
            outputPage(it.path, result)
        }

        logger.trace(exit, '')
    }

    @Override
    String toString() {
        "SimpleStaticSiteGenerator()"
    }

}
