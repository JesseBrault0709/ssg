package com.jessebrault.ssg

import com.jessebrault.ssg.buildscript.DefaultBuildScriptConfiguratorFactory
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import picocli.CommandLine

@CommandLine.Command(
        name = 'build',
        mixinStandardHelpOptions = true,
        description = 'Builds the project.'
)
final class SsgBuild extends AbstractBuildCommand {

    private static final Logger logger = LogManager.getLogger(SsgBuild)

    @Override
    protected Integer doSubCommand() {
        logger.traceEntry()
        def result = 0
        this.requestedBuilds.each {
            def buildResult = this.doSingleBuild(
                    it,
                    [new DefaultBuildScriptConfiguratorFactory(
                            new File('.'),
                            this.staticSiteGenerator::getBuildScriptClassLoader
                    )],
                    this.scriptArgs
            )
            if (buildResult == 1) {
                result = 1
            }
        }
        logger.traceExit(result)
    }

}
