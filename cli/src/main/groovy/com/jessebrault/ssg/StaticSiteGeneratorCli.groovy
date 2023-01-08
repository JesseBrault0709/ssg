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
import com.jessebrault.ssg.text.MarkdownFrontMatterGetter
import com.jessebrault.ssg.text.MarkdownTextRenderer
import com.jessebrault.ssg.text.TextFileTextsProvider
import com.jessebrault.ssg.text.TextType
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.core.config.Configuration
import org.apache.logging.log4j.spi.LoggerContext
import picocli.CommandLine

import java.util.concurrent.Callable

@CommandLine.Command(
        name = 'ssg',
        mixinStandardHelpOptions = true,
        version = '0.0.1-SNAPSHOT',
        description = 'Generates a set of html files from a given configuration.'
)
class StaticSiteGeneratorCli implements Callable<Integer> {

    private static final Logger logger = LogManager.getLogger(StaticSiteGeneratorCli)

    static class LogLevel {
        @CommandLine.Option(names = ['--info']) boolean info
        @CommandLine.Option(names = ['--debug']) boolean debug
        @CommandLine.Option(names = ['--trace']) boolean trace
    }

    static void main(String[] args) {
        System.exit(new CommandLine(StaticSiteGeneratorCli).execute(args))
    }

    @CommandLine.ArgGroup(exclusive = true)
    private LogLevel logLevel

    @Override
    Integer call() throws Exception {
        def context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false)
        def configuration = context.getConfiguration()
        def rootLoggerConfig = configuration.getRootLogger()

        if (this.logLevel.info) {
            rootLoggerConfig.level = Level.INFO
        } else if (this.logLevel.debug) {
            rootLoggerConfig.level = Level.DEBUG
        } else if (this.logLevel.trace) {
            rootLoggerConfig.level = Level.TRACE
        } else {
            rootLoggerConfig.level = Level.WARN
        }

        context.updateLoggers()

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
        ssg.generate(new File('build'), globals)
        return 0
    }

}
