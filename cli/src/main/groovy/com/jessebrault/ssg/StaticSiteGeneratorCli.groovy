package com.jessebrault.ssg

import picocli.CommandLine

@CommandLine.Command(
        name = 'ssg',
        mixinStandardHelpOptions = true,
        version = '0.4.0',
        description = 'A static site generator which can interface with Gradle for high extensibility.',
        subcommands = [SsgInit, SsgBuild, SsgWatch]
)
final class StaticSiteGeneratorCli {

    static void main(String[] args) {
        System.exit(new CommandLine(StaticSiteGeneratorCli).with {
            caseInsensitiveEnumValuesAllowed = true
            execute(args)
        })
    }

}
