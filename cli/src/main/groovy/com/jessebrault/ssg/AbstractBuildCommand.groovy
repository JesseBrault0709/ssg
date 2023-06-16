package com.jessebrault.ssg

import com.jessebrault.ssg.buildscript.BuildScriptConfiguratorFactory
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

    protected CliBasedStaticSiteGenerator staticSiteGenerator = null

    protected final Integer doSingleBuild(
            String requestedBuild,
            Collection<BuildScriptConfiguratorFactory> configuratorFactories,
            Map<String, Object> buildScriptBinding
    ) {
        logger.traceEntry('requestedBuild: {}', requestedBuild)

        if (this.staticSiteGenerator == null) {
            final Collection<URL> buildScriptUrls = []

            def buildScriptParentFile = this.buildScript.parentFile
            if (buildScriptParentFile == null) {
                buildScriptUrls.add(new File('.').toURI().toURL())
            } else {
                buildScriptUrls.add(buildScriptParentFile.toURI().toURL())
            }

            buildScriptUrls.addAll(this.buildSrcDirs.collect { it.toURI().toURL() })

            this.staticSiteGenerator = new CliBasedStaticSiteGenerator(this.buildScript, buildScriptUrls)
        }

        final Collection<Diagnostic> diagnostics = []
        if (!this.staticSiteGenerator.doBuild(
                requestedBuild,
                configuratorFactories,
                buildScriptBinding,
                diagnostics.&addAll
        )) {
            diagnostics.each {
                logger.error(it.message)
                if (it.exception != null) {
                    it.exception.printStackTrace()
                }
            }
            logger.traceExit(1)
        } else {
            logger.traceExit(0)
        }
    }

}
