package com.jessebrault.ssg

import com.jessebrault.ssg.pagetemplate.PageTemplatesFactory
import com.jessebrault.ssg.text.TextFilesFactory
import com.jessebrault.ssg.util.FileNameHandler
import com.jessebrault.ssg.util.RelativePathHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class StaticSiteGeneratorImpl implements StaticSiteGenerator {

    private static final Logger logger = LoggerFactory.getLogger(StaticSiteGeneratorImpl)

    private final Config config
    private final TextFilesFactory textFilesFactory
    private final PageTemplatesFactory pageTemplatesFactory

    StaticSiteGeneratorImpl(Config config) {
        this.config = config
        this.textFilesFactory = config.textFileFactoryGetter.apply(config)
        this.pageTemplatesFactory = config.pageTemplatesFactoryGetter.apply(config)
    }

    @Override
    void generate(SiteSpec spec) {
        def textFiles = this.textFilesFactory.getTextFiles(spec.textsDir)
        def pageTemplates = this.pageTemplatesFactory.getTemplates(spec.templatesDir)
        textFiles.each {
            logger.info('processing textFile: {}', it.relativePath)
            def fileNameHandler = new FileNameHandler(it.file)
            def textFileType = this.config.textFileTypes.find {
                it.extensions.contains(fileNameHandler.getExtension())
            }
            logger.debug('textFileType: {}', textFileType)
            if (textFileType == null) {
                throw new IllegalArgumentException('unknown textFile type: ' + it.relativePath)
            }

            def renderedText = textFileType.renderer.render(it.file.text)
            logger.debug('renderedText: {}', renderedText)

            def frontMatter = textFileType.frontMatterGetter.get(it.file.text)
            logger.debug('frontMatter: {}', frontMatter)

            def desiredPageTemplate = frontMatter['template']
            logger.debug('desiredPageTemplate name: {}', desiredPageTemplate)
            if (desiredPageTemplate == null) {
                throw new IllegalArgumentException('in textFile ' + it.relativePath + ' template must not be null')
            }
            def pageTemplate = pageTemplates.find {
                it.relativePath == desiredPageTemplate
            }
            logger.debug('pageTemplate: {}', pageTemplate)
            if (pageTemplate == null) {
                throw new IllegalArgumentException('in textFile' + it.relativePath + ' unknown pageTemplate: ' + desiredPageTemplate)
            }

            def result = pageTemplate.type.renderer.render(pageTemplate, renderedText)
            logger.debug('result: {}', result)

            def outFile = new File(spec.buildDir, new RelativePathHandler(it.relativePath).getWithoutExtension() + '.html')
            if (outFile.exists()) {
                logger.info('outFile {} already exists, deleting', outFile)
                outFile.delete()
            }
            outFile.createParentDirectories()
            logger.info('writing result to {}', outFile)
            outFile.write(result)
        }
    }

}
