package com.jessebrault.ssg

import com.jessebrault.ssg.gradle.SsgBuildModel
import com.jessebrault.ssg.util.Diagnostic
import com.jessebrault.ssg.util.URLUtil
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.gradle.tooling.GradleConnector
import picocli.CommandLine

import java.nio.file.Files
import java.nio.file.Path

abstract class AbstractBuildCommand extends AbstractSubCommand {

    private static final Logger logger = LogManager.getLogger(AbstractBuildCommand)

    @CommandLine.Option(
            names = ['-b', '--build'],
            description = 'The name of a build to execute. May be the name of a script (without the .groovy extension) in a build script dir, or a fully-qualified-name of a build script on the classpath or nested in a build script dir.',
            arity = '1..*',
            required = true,
            paramLabel = 'buildName'
    )
    Set<String> requestedBuilds

    @CommandLine.Option(
            names = ['--build-script-dir'],
            description = 'A directory containing build scripts, relative to the project directory.',
            arity = '1..*',
            required = true,
            defaultValue = 'ssg',
            paramLabel = 'build-script-dir'
    )
    Set<Path> buildScriptDirs

    @CommandLine.Option(
            names = ['-A', '--script-arg'],
            description = 'Named args to pass directly to the build script.'
    )
    Map<String, String> scriptArgs

    @CommandLine.Option(
            names = ['-g', '--gradle'],
            description = 'If set, build the associated Gradle project and use its output in the ssg build process.',
            negatable = true
    )
    boolean gradle

    @CommandLine.Option(
            names = ['--gradle-project-dir'],
            description = 'The Gradle project directory for the project containing Pages, Components, Models, etc., relative to the project directory.',
            defaultValue = '.'
    )
    Path gradleProjectDir

    @CommandLine.Option(
            names = ['-l', '--lib-dir'],
            description = 'A lib dir where jars containing Pages, Components, Models, etc., can be found, relative to the project directory.',
            defaultValue = 'lib'
    )
    Set<Path> libDirs

    @CommandLine.Option(
            names = ['--dry-run'],
            description = 'Do a dry run of the build; do not actually output anything.'
    )
    boolean dryRun

    protected StaticSiteGenerator staticSiteGenerator = null

    protected final Integer doSingleBuild(String buildName) {
        logger.traceEntry('buildName: {}', buildName)

        if (this.staticSiteGenerator == null) {
            def groovyClassLoader = new GroovyClassLoader(this.class.classLoader)

            if (this.gradle) {
                def projectConnection = GradleConnector.newConnector()
                        .forProjectDirectory(
                                this.commonCliOptions.projectDir.toPath().resolve(this.gradleProjectDir).toFile()
                        ).connect()
                def ssgGradleModel = projectConnection.getModel(SsgBuildModel)

                ssgGradleModel.buildOutputLibs.each { outputLib ->
                    if (outputLib.name.endsWith('.jar')) {
                        groovyClassLoader.addURL(URLUtil.ofJarFile(outputLib))
                    }
                }

                ssgGradleModel.runtimeClasspath.each { classpathElement ->
                    if (classpathElement.name.endsWith('.jar')) {
                        groovyClassLoader.addURL(URLUtil.ofJarFile(classpathElement))
                    }
                }

                projectConnection.newBuild().forTasks('ssgJars').run()
                projectConnection.close()
            }

            this.libDirs.each { libDir ->
                def resolved = this.commonCliOptions.projectDir.toPath().resolve(libDir)
                if (Files.exists(resolved)) {
                    Files.walk(resolved).each {
                        def asFile = it.toFile()
                        if (asFile.isFile() && asFile.name.endsWith('.jar')) {
                            groovyClassLoader.addURL(URLUtil.ofJarFile(asFile))
                        }
                    }
                }
            }

            def buildScriptDirUrls = this.buildScriptDirs.collect {
                def withProjectDir = new File(this.commonCliOptions.projectDir, it.toString())
                withProjectDir.toURI().toURL()
            } as URL[]

            this.staticSiteGenerator = new DefaultStaticSiteGenerator(
                    groovyClassLoader,
                    buildScriptDirUrls,
                    this.dryRun
            )
        }

        final Collection<Diagnostic> diagnostics = this.staticSiteGenerator.doBuild(
                this.commonCliOptions.projectDir,
                buildName,
                buildName,
                this.scriptArgs ?: [:]
        )

        if (!diagnostics.isEmpty()) {
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
