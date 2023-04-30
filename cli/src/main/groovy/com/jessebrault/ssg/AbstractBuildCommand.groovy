package com.jessebrault.ssg

import com.jessebrault.ssg.buildscript.Build
import com.jessebrault.ssg.buildscript.SimpleBuildScriptRunner
import com.jessebrault.ssg.buildscript.DefaultBuildScriptConfiguratorFactory
import com.jessebrault.ssg.util.Diagnostic
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import picocli.CommandLine

abstract class AbstractBuildCommand extends AbstractSubCommand {

    private static final Logger logger = LogManager.getLogger(AbstractBuildCommand)

    protected Collection<Build> availableBuilds = []

    @CommandLine.Option(
            names = ['-b', '--build'],
            description = 'The name of a build to execute.',
            paramLabel = 'buildName'
    )
    protected Collection<String> requestedBuilds = ['default']

    @CommandLine.Option(
            names = ['-s', '--script', '--buildScript'],
            description = 'The build script file to execute.',
            paramLabel = 'buildScript'
    )
    void setBuildFile(File buildScriptFile) {
        if (buildScriptFile == null) {
            buildScriptFile = new File('ssgBuilds.groovy')
        }
        if (buildScriptFile.exists()) {
            logger.info('found buildScriptFile: {}', buildScriptFile)
            def configuratorFactory = new DefaultBuildScriptConfiguratorFactory()
            this.availableBuilds = new SimpleBuildScriptRunner().runBuildScript(
                    buildScriptFile.name,
                    buildScriptFile.parentFile.toURI().toURL(),
                    [new File('buildSrc').toURI().toURL()],
                    [:]
            ) {
                configuratorFactory.get().accept(it)
            }
            logger.debug('after running buildScriptFile {}, builds: {}', buildScriptFile, this.availableBuilds)
        } else {
            throw new IllegalArgumentException(
                    "buildScriptFile file ${ buildScriptFile } does not exist or could not be found."
            )
        }
    }

    protected final Integer doBuild(String requestedBuild) {
        logger.traceEntry('requestedBuild: {}', requestedBuild)

        def buildTasksConverter = new SimpleBuildTasksConverter()

        def build = this.availableBuilds.find { it.name == requestedBuild }

        def hadDiagnostics = false
        def tasksResult = buildTasksConverter.convert(build)
        if (tasksResult.hasDiagnostics()) {
            hadDiagnostics = true
            tasksResult.diagnostics.each { logger.error(it.message) }
        } else {
            def tasks = tasksResult.get()
            Collection<Diagnostic> taskDiagnostics = []
            tasks.each { it.execute(tasks) }
            if (!taskDiagnostics.isEmpty()) {
                hadDiagnostics = true
                taskDiagnostics.each { logger.error(it.message) }
            }
        }

        logger.traceExit(hadDiagnostics ? 1 : 0)
    }

}
