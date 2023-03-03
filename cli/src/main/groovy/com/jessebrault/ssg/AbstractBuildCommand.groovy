package com.jessebrault.ssg

import com.jessebrault.ssg.buildscript.GroovyBuildScriptRunner
import com.jessebrault.ssg.task.Output
import com.jessebrault.ssg.part.GspPartRenderer
import com.jessebrault.ssg.part.PartFilePartsProvider
import com.jessebrault.ssg.part.PartType
import com.jessebrault.ssg.specialpage.GspSpecialPageRenderer
import com.jessebrault.ssg.specialpage.SpecialPageFileSpecialPagesProvider
import com.jessebrault.ssg.specialpage.SpecialPageType
import com.jessebrault.ssg.task.TaskExecutorContext
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

        def defaultTextsProvider = new TextFileTextsProvider(new File('texts'), [markdownText])
        def defaultTemplatesProvider = new TemplateFileTemplatesProvider(new File('templates'), [gspTemplate])
        def defaultPartsProvider = new PartFilePartsProvider(new File('parts'), [gspPart])
        def defaultSpecialPagesProvider = new SpecialPageFileSpecialPagesProvider(new File('specialPages'), [gspSpecialPage])

        def defaultConfig = new Config(
                textProviders: [defaultTextsProvider],
                templatesProviders: [defaultTemplatesProvider],
                partsProviders: [defaultPartsProvider],
                specialPagesProviders: [defaultSpecialPagesProvider]
        )
        def defaultSiteSpec = new SiteSpec(
                name: '',
                baseUrl: ''
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
            builds << new Build(
                    'default',
                    defaultConfig,
                    defaultSiteSpec,
                    defaultGlobals,
                    new File('build')
            )
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
            if (result.hasDiagnostics()) {
                hadDiagnostics = true
                result.diagnostics.each {
                    logger.error(it.message)
                }
            } else {
                def tasks = result.get()
                Collection<Diagnostic> executionDiagnostics = []
                def context = new TaskExecutorContext(
                        it,
                        tasks,
                        this.ssg.taskTypes,
                        { Collection<Diagnostic> diagnostics ->
                            executionDiagnostics.addAll(diagnostics)
                        }
                )
                result.get().each { it.execute(context) }
                if (!executionDiagnostics.isEmpty()) {
                    hadDiagnostics = true
                    executionDiagnostics.each {
                        logger.error(it.message)
                    }
                }
            }
        }

        logger.traceExit(hadDiagnostics ? 1 : 0)
    }

}
