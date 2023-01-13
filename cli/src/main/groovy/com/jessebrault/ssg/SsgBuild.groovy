package com.jessebrault.ssg

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import picocli.CommandLine

@CommandLine.Command(
        name = 'build',
        mixinStandardHelpOptions = true,
        description = 'Builds the project.'
)
class SsgBuild extends AbstractBuildCommand {

    private static final Logger logger = LogManager.getLogger(SsgBuild)

    @Override
    Integer doSubCommand() {
        this.doBuild()
    }

}
