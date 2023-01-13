package com.jessebrault.ssg

import picocli.CommandLine

@CommandLine.Command(
        name = 'ssg',
        mixinStandardHelpOptions = true,
        version = '0.0.1-SNAPSHOT',
        description = 'Generates a set of html files from a given configuration.',
        subcommands = [SsgInit, SsgBuild, SsgWatch]
)
class StaticSiteGeneratorCli {

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

}
