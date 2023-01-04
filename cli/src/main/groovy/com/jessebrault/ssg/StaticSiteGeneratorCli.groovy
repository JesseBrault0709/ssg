package com.jessebrault.ssg

import com.jessebrault.ssg.frontmatter.MarkdownFrontMatterGetter
import com.jessebrault.ssg.renderer.GspRenderer
import com.jessebrault.ssg.template.TemplatesFactoryImpl
import com.jessebrault.ssg.textfile.TextFilesFactoryImpl
import com.jessebrault.ssg.textrenderer.MarkdownRenderer

class StaticSiteGeneratorCli {

    static void main(String[] args) {
        def config = new Config(
                textFilesFactory: new TextFilesFactoryImpl(),
                templatesFactory: new TemplatesFactoryImpl(),
                markdownFrontMatterGetter: new MarkdownFrontMatterGetter(),
                markdownRenderer: new MarkdownRenderer(),
                gspRenderer: new GspRenderer()
        )
        def ssg = new StaticSiteGeneratorImpl(config)
        def spec = new SiteSpec(
                buildDir: new File('build'),
                staticDir: new File('static'),
                textsDir: new File('texts'),
                templatesDir: new File('templates'),
                templatePartsDir: new File('templatePartsDir')
        )
        ssg.generate(spec)
    }

}
