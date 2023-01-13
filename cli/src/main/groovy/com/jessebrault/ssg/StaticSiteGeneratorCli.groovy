package com.jessebrault.ssg

import com.jessebrault.ssg.buildscript.GroovyBuildScriptRunner
import com.jessebrault.ssg.part.GspPartRenderer
import com.jessebrault.ssg.part.PartFilePartsProvider
import com.jessebrault.ssg.part.PartType
import com.jessebrault.ssg.provider.WithWatchableDir
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
import groovy.io.FileType
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.core.LoggerContext
import picocli.CommandLine

import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchEvent
import java.nio.file.WatchKey
import java.util.concurrent.Callable

@CommandLine.Command(
        name = 'ssg',
        mixinStandardHelpOptions = true,
        version = '0.0.1-SNAPSHOT',
        description = 'Generates a set of html files from a given configuration.'
)
class StaticSiteGeneratorCli implements Callable<Integer> {

    private static final Logger logger = LogManager.getLogger(StaticSiteGeneratorCli)

    static void main(String[] args) {
        System.exit(new CommandLine(StaticSiteGeneratorCli).execute(args))
    }

    static class LogLevel {

        @CommandLine.Option(names = ['--info'], description = 'Log at INFO level.')
        boolean info

        @CommandLine.Option(names = ['--debug'], description = 'Log at DEBUG level.')
        boolean debug

        @CommandLine.Option(names = ['--trace'], description = 'Log at TRACE level.')
        boolean trace

    }

    @CommandLine.ArgGroup(exclusive = true, heading = 'Log Level')
    LogLevel logLevel

    @CommandLine.Option(names = ['-w', '--watch'], description = 'Run in watch mode.')
    boolean watch

    @Override
    Integer call() {
        logger.traceEntry()

        // Setup Loggers
        def context = (LoggerContext) LogManager.getContext(false)
        def configuration = context.getConfiguration()
        def rootLoggerConfig = configuration.getRootLogger()

        if (this.logLevel?.info) {
            rootLoggerConfig.level = Level.INFO
        } else if (this.logLevel?.debug) {
            rootLoggerConfig.level = Level.DEBUG
        } else if (this.logLevel?.trace) {
            rootLoggerConfig.level = Level.TRACE
        } else {
            rootLoggerConfig.level = Level.WARN
        }

        context.updateLoggers()

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

        Collection<Build> builds = []

        // Run build script, if applicable
        if (new File('ssgBuilds.groovy').exists()) {
            logger.info('found buildScript: ssgBuilds.groovy')
            def buildScriptRunner = new GroovyBuildScriptRunner()
            builds.addAll(buildScriptRunner.runBuildScript('ssgBuilds.groovy', defaultConfig, defaultGlobals))
            logger.debug('after running ssgBuilds.groovy, builds: {}', builds)
        }

        if (builds.empty) {
            // Add default build
            builds << new Build('default', defaultConfig, defaultGlobals, new File('build'))
        }

        // Get ssg object
        def ssg = new SimpleStaticSiteGenerator()

        if (this.watch) {
            generate(builds, ssg)
            watch(builds, ssg)
        } else {
            generate(builds, ssg)
        }
    }

    private static Integer generate(Collection<Build> builds, StaticSiteGenerator ssg) {
        logger.traceEntry('builds: {}, ssg: {}', builds, ssg)

        def hadDiagnostics = false
        // Do each build
        builds.each {
            def result = ssg.generate(it)
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

    private static Integer watch(Collection<Build> builds, StaticSiteGenerator ssg) {
        logger.traceEntry('builds: {}, ssg: {}', builds, ssg)

        // Setup watchService and watchKeys
        def watchService = FileSystems.getDefault().newWatchService()
        Map<WatchKey, Path> watchKeys = [:]

        // Our Closure to register a directory path
        def registerPath = { Path path ->
            if (!Files.isDirectory(path)) {
                throw new IllegalArgumentException('path must be a directory, given: ' + path)
            }
            logger.debug('registering dir with path: {}', path)
            def watchKey = path.register(
                    watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY
            )
            watchKeys[watchKey] = path
            logger.debug('watchKeys: {}', watchKeys)
        }

        // Get all base watchableDirs
        Collection<WithWatchableDir> watchableProviders = []
        builds.each {
            it.config.textProviders.each {
                if (it instanceof WithWatchableDir) {
                    watchableProviders << it
                }
            }
            it.config.templatesProviders.each {
                if (it instanceof WithWatchableDir) {
                    watchableProviders << it
                }
            }
            it.config.partsProviders.each {
                if (it instanceof WithWatchableDir) {
                    watchableProviders << it
                }
            }
            it.config.specialPagesProviders.each {
                if (it instanceof WithWatchableDir) {
                    watchableProviders << it
                }
            }
        }
        // register them and their child directories using the Closure above
        watchableProviders.each {
            def baseDirFile = it.watchableDir
            registerPath(baseDirFile.toPath())
            baseDirFile.eachFile(FileType.DIRECTORIES) {
                registerPath(it.toPath())
            }
        }

        //noinspection GroovyInfiniteLoopStatement
        while (true) {
            def watchKey = watchService.take()
            def path = watchKeys[watchKey]
            if (path == null) {
                logger.warn('unexpected watchKey: {}', watchKey)
            } else {
                watchKey.pollEvents().each {
                    assert it instanceof WatchEvent<Path>
                    def childName = it.context()
                    def childPath = path.resolve(childName)
                    if (it.kind() == StandardWatchEventKinds.ENTRY_CREATE && Files.isDirectory(childPath)) {
                        registerPath(childPath)
                    } else if (Files.isRegularFile(childPath)) {
                        logger.debug('detected {} for regularFile with path {}', it.kind(), childPath)
                        def t = new Thread({
                            generate(builds, ssg)
                        })
                        t.setName('workerThread')
                        t.start()
                    }
                }
            }
            def valid = watchKey.reset()
            if (!valid) {
                def removedPath = watchKeys.remove(watchKey)
                logger.debug('removed path: {}', removedPath)
            }
        }

        //noinspection GroovyUnreachableStatement
        logger.traceExit(0)
    }

}
