package com.jessebrault.ssg

import picocli.CommandLine

class CommonCliOptions {

    @CommandLine.Option(names = '--project-dir', defaultValue = '.', description = 'The ssg project directory.')
    File projectDir

    @CommandLine.Option(names = '--log-level', defaultValue = 'info', description = 'The logging level.')
    LogLevel logLevel

}
