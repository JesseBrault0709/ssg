package com.jessebrault.ssg

import com.jessebrault.ssg.buildscript.Build
import com.jessebrault.ssg.buildscript.BuildScriptConfiguratorFactory
import com.jessebrault.ssg.buildscript.BuildScriptRunner
import com.jessebrault.ssg.util.Diagnostic
import org.jetbrains.annotations.Nullable
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.Marker
import org.slf4j.MarkerFactory

import java.util.function.Consumer

final class BuildScriptBasedStaticSiteGenerator implements StaticSiteGenerator {

    private static final Logger logger = LoggerFactory.getLogger(BuildScriptBasedStaticSiteGenerator)
    private static final Marker enter = MarkerFactory.getMarker('enter')
    private static final Marker exit = MarkerFactory.getMarker('exit')

    private final @Nullable File buildScript
    private final Collection<Build> builds = []

    private boolean ranBuildScript = false
    private GroovyClassLoader buildScriptClassLoader

    /**
     * @param buildScriptClassLoaderUrls the urls to pass to the buildScriptRunner's GroovyClassLoader.
     *          These should include the URL needed to find the given buildScript file, if present, as
     *          well as any script dependencies (such as the buildSrc dir).
     * @param buildScript The buildScript File, may be <code>null</code>.
     */
    BuildScriptBasedStaticSiteGenerator(
            Collection<URL> buildScriptClassLoaderUrls,
            @Nullable File buildScript = null
    ) {
        this.buildScript = buildScript
    }

    private void runBuildScript(
            Collection<BuildScriptConfiguratorFactory> configuratorFactories,
            Map<String, Object> buildScriptArgs
    ) {
        logger.trace(enter, 'configuratorFactories: {}, buildScriptArgs: {}', configuratorFactories, buildScriptArgs)

        if (this.buildScript == null) {
            logger.info('no specified build script; using defaults')
            def result = BuildScriptRunner.runClosureScript { base ->
                configuratorFactories.each {
                    it.get().accept(base)
                }
            }
            this.builds.addAll(result)
        } else if (this.buildScript.exists() && this.buildScript.isFile()) {
            logger.info('running buildScript: {}', this.buildScript)
            def buildScriptRunner = new BuildScriptRunner([
                    this.buildScript.parentFile.toURI().toURL()
            ])
            this.buildScriptClassLoader = buildScriptRunner.getBuildScriptClassLoader()
            def result = buildScriptRunner.runBuildScript(
                    this.buildScript.name,
                    [args: buildScriptArgs]
            ) { base ->
                configuratorFactories.each { it.get().accept(base) }
            }
            this.builds.addAll(result)
        } else {
            throw new IllegalArgumentException("given buildScript ${ this.buildScript } either does not exist or is not a file")
        }
        this.ranBuildScript = true

        logger.trace(exit, '')
    }

    /**
     * @return The classLoader used to load the buildScript.
     * @throws NullPointerException if the buildScriptRunner was not initialized yet (make sure to call
     *          {@link #doBuild} first).
     */
    GroovyClassLoader getBuildScriptClassLoader() {
        Objects.requireNonNull(this.buildScriptClassLoader)
    }

    // TODO: cache
    @Override
    boolean doBuild(
            String buildName,
            Collection<BuildScriptConfiguratorFactory> configuratorFactories,
            Map<String, Object> buildScriptArgs,
            Consumer<Collection<Diagnostic>> diagnosticsConsumer
    ) {
        logger.trace(enter, 'buildName: {}, diagnosticsConsumer: {}', buildName, diagnosticsConsumer)

        if (!this.ranBuildScript) {
            this.runBuildScript(configuratorFactories, buildScriptArgs)
        }

        def build = this.builds.find { it.name == buildName }
        if (!build) {
            throw new IllegalArgumentException("there is no registered build with name: ${ buildName }")
        }

        def buildTasksConverter = new SimpleBuildTasksConverter()
        def successful = true
        def tasksResult = buildTasksConverter.convert(build)
        if (tasksResult.hasDiagnostics()) {
            successful = false
            diagnosticsConsumer.accept(tasksResult.diagnostics)
        } else {
            def tasks = tasksResult.get()
            def taskDiagnostics = tasks.collectMany { it.execute(tasks) }
            if (!taskDiagnostics.isEmpty()) {
                successful = false
                diagnosticsConsumer.accept(taskDiagnostics)
            }
        }

        logger.trace(exit, 'successful: {}', successful)
        successful
    }

}
