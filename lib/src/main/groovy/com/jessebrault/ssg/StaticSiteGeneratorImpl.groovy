package com.jessebrault.ssg

import com.jessebrault.ssg.frontmatter.FrontMatterGetter
import com.jessebrault.ssg.template.TemplatesFactory
import com.jessebrault.ssg.renderer.Renderer
import com.jessebrault.ssg.textfile.TextFile
import com.jessebrault.ssg.textfile.TextFilesFactory
import com.jessebrault.ssg.textrenderer.TextRenderer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class StaticSiteGeneratorImpl implements StaticSiteGenerator {

    private static final Logger logger = LoggerFactory.getLogger(StaticSiteGeneratorImpl)

    private static String stripExtension(String relativePath) {
        relativePath.substring(0, relativePath.lastIndexOf('.'))
    }

    private final TextFilesFactory textFilesFactory
    private final TemplatesFactory templateFactory
    private final FrontMatterGetter markdownFrontMatterGetter
    private final TextRenderer markdownRenderer
    private final Renderer gspRenderer

    StaticSiteGeneratorImpl(Config config) {
        this.textFilesFactory = Objects.requireNonNull(config.textFilesFactory)
        this.templateFactory = Objects.requireNonNull(config.templatesFactory)
        this.markdownFrontMatterGetter = Objects.requireNonNull(config.markdownFrontMatterGetter)
        this.markdownRenderer = Objects.requireNonNull(config.markdownRenderer)
        this.gspRenderer = Objects.requireNonNull(config.gspRenderer)
    }

    @Override
    void generate(SiteSpec spec) {
        def textFiles = this.textFilesFactory.getTextFiles(spec.textsDir)
        def templates = this.templateFactory.getTemplates(spec.templatesDir)
        textFiles.each {
            if (it.type == TextFile.Type.MARKDOWN) {
                def frontMatter = this.markdownFrontMatterGetter.get(it.file.text)
                def desiredTemplate = frontMatter['template']
                if (desiredTemplate == null) {
                    throw new IllegalArgumentException('template must not be null')
                }
                def template = templates.find { it.relativePath == desiredTemplate }
                def renderedText = this.markdownRenderer.render(it.file.text)
                def result = this.gspRenderer.render(template, renderedText)
                def outFile = new File(spec.buildDir, stripExtension(it.relativePath) + '.html')
                if (outFile.exists()) {
                    outFile.delete()
                }
                outFile.createParentDirectories()
                logger.debug('writing to outFile {}', outFile)
                outFile.write(result)
            }
        }
    }

}
