package com.jessebrault.ssg

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.core.LoggerContext
import picocli.CommandLine

import java.util.concurrent.Callable

abstract class AbstractSubCommand implements Callable<Integer> {

    private static final Logger logger = LogManager.getLogger(AbstractSubCommand)

    @CommandLine.Mixin
    CommonCliOptions commonCliOptions

    protected abstract Integer doSubCommand()

    @Override
    Integer call() {
        logger.traceEntry()

        // Setup Loggers
        def context = (LoggerContext) LogManager.getContext(false)
        def configuration = context.getConfiguration()
        def rootLoggerConfig = configuration.getRootLogger()

        def logLevel = this.commonCliOptions.logLevel
        if (logLevel == LogLevel.INFO) {
            rootLoggerConfig.level = Level.INFO
        } else if (logLevel == LogLevel.DEBUG) {
            rootLoggerConfig.level = Level.DEBUG
        } else if (logLevel == LogLevel.TRACE) {
            rootLoggerConfig.level = Level.TRACE
        } else {
            rootLoggerConfig.level = Level.INFO
        }

        context.updateLoggers()

        // Run SubCommand
        logger.traceExit(this.doSubCommand())
    }

}
