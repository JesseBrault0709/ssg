package com.jessebrault.ssg

import com.jessebrault.ssg.buildscript.Build
import com.jessebrault.ssg.buildscript.BuildScriptConfiguratorFactory
import com.jessebrault.ssg.buildscript.FileBuildScriptGetter
import com.jessebrault.ssg.util.Diagnostic
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor
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

    private final Collection<URL> buildScriptClassLoaderUrls
    private final @Nullable File buildScript
    private final Collection<Build> builds = []

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
        this.buildScriptClassLoaderUrls = buildScriptClassLoaderUrls
        this.buildScript = buildScript
    }

    private void runBuildScript(
            Collection<BuildScriptConfiguratorFactory> configuratorFactories,
            Map<String, Object> buildScriptArgs
    ) {
        logger.trace(enter, 'configuratorFactories: {}, buildScriptArgs: {}', configuratorFactories, buildScriptArgs)

        if (this.buildScript == null) {
            logger.info('no specified build script; using defaults')
            def result = FileBuildScriptGetter.runClosureScript { base ->
                configuratorFactories.each {
                    it.get().accept(base)
                }
            }
            this.builds.addAll(result)
        } else if (this.buildScript.exists() && this.buildScript.isFile()) {
            logger.info('running buildScript: {}', this.buildScript)
            def buildScriptRunner = new FileBuildScriptGetter(this.buildScriptClassLoaderUrls)
            this.buildScriptClassLoader = buildScriptRunner.getBuildScriptClassLoader()
            def result = buildScriptRunner.getBuildInfo(
                    this.buildScript.name,
                    [args: buildScriptArgs]
            ) { base ->
                configuratorFactories.each { it.get().accept(base) }
            }
            this.builds.addAll(result)
        } else {
            throw new IllegalArgumentException("given buildScript ${ this.buildScript } either does not exist or is not a file")
        }

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

    @TupleConstructor(defaults = false)
    @NullCheck(includeGenerated = true)
    @EqualsAndHashCode
    private static final class IncludedBuildsResult {
        final Collection<Build> builds
        final Collection<Diagnostic> diagnostics
    }

    // TODO: cache build script results
    @Override
    boolean doBuild(
            String buildName,
            Collection<BuildScriptConfiguratorFactory> configuratorFactories,
            Map<String, Object> buildScriptArgs,
            Consumer<Collection<Diagnostic>> diagnosticsConsumer
    ) {
        logger.trace(enter, 'buildName: {}, diagnosticsConsumer: {}', buildName, diagnosticsConsumer)

        this.runBuildScript(configuratorFactories, buildScriptArgs)

        def build = this.builds.find { it.name == buildName }
        if (!build) {
            throw new IllegalArgumentException("there is no registered build with name: ${ buildName }")
        }

        def includedBuildsResult = build.includedBuilds.inject(
                new IncludedBuildsResult([], [])
        ) { acc, includedBuildName ->
            def includedBuild = this.builds.find { it.name == includedBuildName }
            if (includedBuild == null) {
                acc.diagnostics << new Diagnostic("There is no registered build ${ includedBuildName } that can be included.")
            } else {
                acc.builds << includedBuild
            }
            acc
        }

        if (includedBuildsResult.diagnostics.size() > 0) {
            diagnosticsConsumer.accept(includedBuildsResult.diagnostics)
            logger.trace(exit, 'result: false')
            return false
        }

        def buildTasksConverter = new SimpleBuildTasksConverter()

        def allBuilds = includedBuildsResult.builds + build
        def allBuildsConvertResults = allBuilds.collect {
            buildTasksConverter.convert(it)
        }
        def allBuildsConvertDiagnostics = allBuildsConvertResults.collectMany {
            it.diagnostics
        }

        if (allBuildsConvertDiagnostics.size() > 0) {
            diagnosticsConsumer.accept(allBuildsConvertDiagnostics)
            logger.trace(exit, 'result: false')
            return false
        }

        def allTasks = allBuildsConvertResults.collectMany {
            it.get()
        }
        def allTasksDiagnostics = allTasks.collectMany { it.execute(allTasks) }
        if (allTasksDiagnostics.size() > 0) {
            diagnosticsConsumer.accept(allTasksDiagnostics)
            logger.trace(exit, 'result: false')
            return false
        }

        logger.trace(exit, 'result: true')
        return true
    }

}
