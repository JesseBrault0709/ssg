package com.jessebrault.ssg

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.core.LoggerContext
import picocli.CommandLine

import java.util.concurrent.Callable

abstract class AbstractSubCommand implements Callable<Integer> {

    private static final Logger logger = LogManager.getLogger(AbstractSubCommand)

    @CommandLine.ParentCommand
    StaticSiteGeneratorCli cli

    abstract Integer doSubCommand()

    @Override
    Integer call() {
        logger.traceEntry()

        // Setup Loggers
        def context = (LoggerContext) LogManager.getContext(false)
        def configuration = context.getConfiguration()
        def rootLoggerConfig = configuration.getRootLogger()

        if (this.cli.logLevel?.info) {
            rootLoggerConfig.level = Level.INFO
        } else if (this.cli.logLevel?.debug) {
            rootLoggerConfig.level = Level.DEBUG
        } else if (this.cli.logLevel?.trace) {
            rootLoggerConfig.level = Level.TRACE
        } else {
            rootLoggerConfig.level = Level.WARN
        }

        context.updateLoggers()

        // Run SubCommand
        logger.traceExit(this.doSubCommand())
    }

}
