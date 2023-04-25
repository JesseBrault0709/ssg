package com.jessebrault.ssg

import com.jessebrault.ssg.buildscript.Build
import com.jessebrault.ssg.buildscript.BuildScriptUtil
import com.jessebrault.ssg.buildscript.DefaultBuildScriptConfiguratorFactory
import com.jessebrault.ssg.util.Diagnostic
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

abstract class AbstractBuildCommand extends AbstractSubCommand {

    private static final Logger logger = LogManager.getLogger(AbstractBuildCommand)

    protected final Collection<Build> builds = []

    AbstractBuildCommand() {
        // Run build script
        if (new File('ssgBuilds.groovy').exists()) {
            logger.info('found buildScript: ssgBuilds.groovy')
            def configuratorFactory = new DefaultBuildScriptConfiguratorFactory()
            this.builds.addAll(BuildScriptUtil.runBuildScript('ssgBuilds.groovy') {
                configuratorFactory.get().accept(it)
            })
            logger.debug('after running ssgBuilds.groovy, builds: {}', this.builds)
        } else {
            throw new IllegalArgumentException('ssgBuilds.groovy could not be found')
        }
    }

    protected final Integer doBuild() {
        logger.traceEntry('builds: {}', this.builds)

        def buildTasksConverter = new SimpleBuildTasksConverter()

        def hadDiagnostics = false
        // Do each build
        this.builds.each {
            def tasksResult = buildTasksConverter.convert(it)
            if (tasksResult.hasDiagnostics()) {
                hadDiagnostics = true
                tasksResult.diagnostics.each {
                    logger.error(it.message)
                }
            } else {
                def tasks = tasksResult.get()
                Collection<Diagnostic> taskDiagnostics = []
                tasks.each { it.execute(tasks) }
                if (!taskDiagnostics.isEmpty()) {
                    hadDiagnostics = true
                    taskDiagnostics.each {
                        logger.error(it.message)
                    }
                }
            }
        }

        logger.traceExit(hadDiagnostics ? 1 : 0)
    }

}
