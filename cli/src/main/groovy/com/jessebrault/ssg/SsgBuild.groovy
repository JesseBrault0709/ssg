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
        def tmpDir = File.createTempDir()
        def urls = [tmpDir, *this.buildSrcDirs, new File('.')].collect {
            it.toURI().toURL()
        } as URL[]
        def engine = new GroovyScriptEngine(urls)
        this.requestedBuilds.each {
            def buildResult = this.doSingleBuild(it, tmpDir, engine)
            if (buildResult == 1) {
                result = 1
            }
        }
        logger.traceExit(result)
    }

}
