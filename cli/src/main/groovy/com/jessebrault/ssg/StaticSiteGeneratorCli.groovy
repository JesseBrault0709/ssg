package com.jessebrault.ssg

import com.jessebrault.ssg.buildscript.GroovyBuildScriptRunner
import com.jessebrault.ssg.part.GspPartRenderer
import com.jessebrault.ssg.part.PartFilePartsProvider
import com.jessebrault.ssg.part.PartType
import com.jessebrault.ssg.specialpage.GspSpecialPageRenderer
import com.jessebrault.ssg.specialpage.SpecialPageFileSpecialPagesProvider
import com.jessebrault.ssg.specialpage.SpecialPageType
import com.jessebrault.ssg.template.GspTemplateRenderer
import com.jessebrault.ssg.template.TemplateType
import com.jessebrault.ssg.template.TemplateFileTemplatesProvider
import com.jessebrault.ssg.text.MarkdownFrontMatterGetter
import com.jessebrault.ssg.text.MarkdownTextRenderer
import com.jessebrault.ssg.text.TextType
import com.jessebrault.ssg.text.TextFileTextsProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class StaticSiteGeneratorCli {

    private static final Logger logger = LoggerFactory.getLogger(StaticSiteGeneratorCli)

    static void main(String[] args) {
        def markdownText = new TextType(['.md'], new MarkdownTextRenderer(), new MarkdownFrontMatterGetter())
        def gspTemplate = new TemplateType(['.gsp'], new GspTemplateRenderer())
        def gspPart = new PartType(['.gsp'], new GspPartRenderer())
        def gspSpecialPage = new SpecialPageType(['.gsp'], new GspSpecialPageRenderer())

        def defaultTextsProvider = new TextFileTextsProvider([markdownText], new File('texts'))
        def defaultTemplatesProvider = new TemplateFileTemplatesProvider([gspTemplate], new File('templates'))
        def defaultPartsProvider = new PartFilePartsProvider([gspPart], new File('parts'))
        def defaultSpecialPagesProvider = new SpecialPageFileSpecialPagesProvider([gspSpecialPage], new File('specialPages'))

        def config = new Config(
                textProviders: [defaultTextsProvider],
                templatesProviders: [defaultTemplatesProvider],
                partsProviders: [defaultPartsProvider],
                specialPagesProviders: [defaultSpecialPagesProvider]
        )

        def globals = [:]

        if (new File('build.groovy').exists()) {
            logger.info('found buildScript: build.groovy')
            def buildScriptRunner = new GroovyBuildScriptRunner()
            buildScriptRunner.runBuildScript(config, globals)
            logger.debug('after running buildScript, config: {}', config)
            logger.debug('after running buildScript, globals: {}', globals)
        }

        def ssg = new SimpleStaticSiteGenerator(config)
        ssg.generate(new File('build'))
    }

}
