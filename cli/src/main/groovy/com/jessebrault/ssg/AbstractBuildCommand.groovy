package com.jessebrault.ssg

import com.jessebrault.ssg.util.Diagnostic
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import picocli.CommandLine

abstract class AbstractBuildCommand extends AbstractSubCommand {

    private static final Logger logger = LogManager.getLogger(AbstractBuildCommand)

    @CommandLine.Option(
            names = ['-s', '--script', '--buildScript'],
            description = 'The build script file to execute.'
    )
    File buildScript = new File('ssgBuilds.groovy')

    @CommandLine.Option(
            names = '--scriptArgs',
            description = 'Named argument(s) to pass directly to the build script.',
            split = ','
    )
    Map<String, String> scriptArgs = [:]

    @CommandLine.Option(
            names = '--buildSrcDirs',
            description = 'Path(s) to director(ies) containing Groovy classes and scripts which should be visible to the main build script.',
            split = ',',
            paramLabel = 'buildSrcDir'
    )
    Collection<File> buildSrcDirs = [new File('buildSrc')]

    @CommandLine.Option(
            names = ['-b', '--build'],
            description = 'The name of a build to execute.',
            paramLabel = 'buildName'
    )
    Collection<String> requestedBuilds = ['default']

    protected StaticSiteGenerator staticSiteGenerator = null

    protected final Integer doSingleBuild(String requestedBuild, File tmpDir, GroovyScriptEngine engine) {
        logger.traceEntry('requestedBuild: {}', requestedBuild)

        if (this.staticSiteGenerator == null) {
            this.staticSiteGenerator = new CliBasedStaticSiteGenerator(
                    new File('.'),
                    this.buildScript,
                    tmpDir,
                    engine,
                    this.scriptArgs
            )
        }

        final Collection<Diagnostic> diagnostics = []
        if (!this.staticSiteGenerator.doBuild(requestedBuild, diagnostics.&addAll)) {
            diagnostics.each { logger.warn(it) }
            logger.traceExit(1)
        } else {
            logger.traceExit(0)
        }
    }

}
