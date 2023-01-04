package com.jessebrault.ssg

import com.jessebrault.ssg.pagetemplate.GspRenderer
import com.jessebrault.ssg.pagetemplate.PageTemplateType
import com.jessebrault.ssg.pagetemplate.PageTemplatesFactoryImpl
import com.jessebrault.ssg.text.MarkdownFrontMatterGetter
import com.jessebrault.ssg.text.MarkdownRenderer
import com.jessebrault.ssg.text.TextFileType
import com.jessebrault.ssg.text.TextFilesFactoryImpl

class StaticSiteGeneratorCli {

    static void main(String[] args) {
        def markdown = new TextFileType(['.md'], new MarkdownRenderer(), new MarkdownFrontMatterGetter())
        def gsp = new PageTemplateType(['.gsp'], new GspRenderer())
        def config = new Config(
                textFileTypes: [markdown],
                pageTemplateTypes: [gsp],
                textFileFactoryGetter: { Config config -> new TextFilesFactoryImpl(config.textFileTypes) },
                pageTemplatesFactoryGetter: { Config config -> new PageTemplatesFactoryImpl(config.pageTemplateTypes) }
        )
        def ssg = new StaticSiteGeneratorImpl(config)
        def defaultSpec = new SiteSpec(
                buildDir: new File('build'),
                textsDir: new File('texts'),
                templatesDir: new File('templates')
        )
        ssg.generate(defaultSpec)
    }

}
