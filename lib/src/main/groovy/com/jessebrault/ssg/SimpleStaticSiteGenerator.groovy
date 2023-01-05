package com.jessebrault.ssg

import com.jessebrault.ssg.part.PartsProvider
import com.jessebrault.ssg.template.TemplatesProvider
import com.jessebrault.ssg.text.TextsProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.Marker
import org.slf4j.MarkerFactory

class SimpleStaticSiteGenerator implements StaticSiteGenerator {

    private static final Logger logger = LoggerFactory.getLogger(SimpleStaticSiteGenerator)
    private static final Marker enter = MarkerFactory.getMarker('ENTER')
    private static final Marker exit = MarkerFactory.getMarker('EXIT')

    private final Config config
    private final TextsProvider textsProvider
    private final TemplatesProvider templatesProvider
    private final PartsProvider partsProvider

    SimpleStaticSiteGenerator(Config config) {
        this.config = config
        this.textsProvider = config.textsProviderGetter.apply(config)
        this.templatesProvider = config.templatesProviderGetter.apply(config)
        this.partsProvider = config.partsProviderGetter.apply(config)
    }

    @Override
    void generate(File buildDir) {
        logger.trace(enter, 'buildDir: {}', buildDir)

        // Get all texts, templates, and parts
        def texts = this.textsProvider.getTextFiles()
        def templates = this.templatesProvider.getTemplates()
        def parts = this.partsProvider.getParts()

        logger.debug('texts: {}, templates: {}, parts: {}', texts, templates, parts)
        texts.each {
            logger.info('processing text: {}', it.path)

            // Render the text (i.e., transform text to html)
            def renderedText = it.type.renderer.render(it.text)
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
            def template = templates.find {
                it.relativePath == desiredTemplate
            }
            logger.debug('template: {}', template)
            if (template == null) {
                throw new IllegalArgumentException('in textFile' + it.path + ' unknown template: ' + desiredTemplate)
            }

            // Render the template using the result of rendering the text earlier
            def result = template.type.renderer.render(template, frontMatter, renderedText, parts)
            logger.debug('result: {}', result)

            // Output the result to the outfile, an .html file
            def outFile = new File(buildDir, it.path + '.html')
            if (outFile.exists()) {
                logger.info('outFile {} already exists, deleting', outFile)
                outFile.delete()
            }
            outFile.createParentDirectories()
            logger.info('writing result to {}', outFile)
            outFile.write(result)
        }
        logger.trace(exit, '')
    }

}
