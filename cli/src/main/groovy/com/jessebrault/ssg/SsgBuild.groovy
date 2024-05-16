package com.jessebrault.ssg

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
            if (this.doSingleBuild(it) != 0) {
                result = 1
            }
        }
        logger.traceExit(result)
    }

}
