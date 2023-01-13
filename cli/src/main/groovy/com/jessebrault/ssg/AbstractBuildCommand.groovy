package com.jessebrault.ssg

import com.jessebrault.ssg.buildscript.GroovyBuildScriptRunner
import com.jessebrault.ssg.part.GspPartRenderer
import com.jessebrault.ssg.part.PartFilePartsProvider
import com.jessebrault.ssg.part.PartType
import com.jessebrault.ssg.specialpage.GspSpecialPageRenderer
import com.jessebrault.ssg.specialpage.SpecialPageFileSpecialPagesProvider
import com.jessebrault.ssg.specialpage.SpecialPageType
import com.jessebrault.ssg.template.GspTemplateRenderer
import com.jessebrault.ssg.template.TemplateFileTemplatesProvider
import com.jessebrault.ssg.template.TemplateType
import com.jessebrault.ssg.text.MarkdownExcerptGetter
import com.jessebrault.ssg.text.MarkdownFrontMatterGetter
import com.jessebrault.ssg.text.MarkdownTextRenderer
import com.jessebrault.ssg.text.TextFileTextsProvider
import com.jessebrault.ssg.text.TextType
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

abstract class AbstractBuildCommand extends AbstractSubCommand {

    private static final Logger logger = LogManager.getLogger(AbstractBuildCommand)

    protected final Collection<Build> builds = []
    protected final StaticSiteGenerator ssg

    AbstractBuildCommand() {
        // Configure
        def markdownText = new TextType(['.md'], new MarkdownTextRenderer(), new MarkdownFrontMatterGetter(), new MarkdownExcerptGetter())
        def gspTemplate = new TemplateType(['.gsp'], new GspTemplateRenderer())
        def gspPart = new PartType(['.gsp'], new GspPartRenderer())
        def gspSpecialPage = new SpecialPageType(['.gsp'], new GspSpecialPageRenderer())

        def defaultTextsProvider = new TextFileTextsProvider([markdownText], new File('texts'))
        def defaultTemplatesProvider = new TemplateFileTemplatesProvider([gspTemplate], new File('templates'))
        def defaultPartsProvider = new PartFilePartsProvider([gspPart], new File('parts'))
        def defaultSpecialPagesProvider = new SpecialPageFileSpecialPagesProvider([gspSpecialPage], new File('specialPages'))

        def defaultConfig = new Config(
                textProviders: [defaultTextsProvider],
                templatesProviders: [defaultTemplatesProvider],
                partsProviders: [defaultPartsProvider],
                specialPagesProviders: [defaultSpecialPagesProvider]
        )
        def defaultGlobals = [:]

        // Run build script, if applicable
        if (new File('ssgBuilds.groovy').exists()) {
            logger.info('found buildScript: ssgBuilds.groovy')
            def buildScriptRunner = new GroovyBuildScriptRunner()
            this.builds.addAll(buildScriptRunner.runBuildScript('ssgBuilds.groovy', defaultConfig, defaultGlobals))
            logger.debug('after running ssgBuilds.groovy, builds: {}', this.builds)
        }

        if (this.builds.empty) {
            // Add default build
            builds << new Build('default', defaultConfig, defaultGlobals, new File('build'))
        }

        // Get ssg object
        this.ssg = new SimpleStaticSiteGenerator()
    }

    protected final Integer doBuild() {
        logger.traceEntry('builds: {}, ssg: {}', this.builds, this.ssg)

        def hadDiagnostics = false
        // Do each build
        this.builds.each {
            def result = this.ssg.generate(it)
            if (result.v1.size() > 0) {
                hadDiagnostics = true
                result.v1.each {
                    logger.error(it.message)
                }
            } else {
                result.v2.each { GeneratedPage generatedPage ->
                    def target = new File(it.outDir, generatedPage.path + '.html')
                    target.createParentDirectories()
                    target.write(generatedPage.html)
                }
            }
        }

        logger.traceExit(hadDiagnostics ? 1 : 0)
    }

}
