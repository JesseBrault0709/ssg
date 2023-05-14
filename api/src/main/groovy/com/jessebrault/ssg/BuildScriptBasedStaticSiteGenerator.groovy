package com.jessebrault.ssg

import com.jessebrault.ssg.buildscript.Build
import com.jessebrault.ssg.buildscript.BuildScriptConfiguratorFactory
import com.jessebrault.ssg.buildscript.BuildScripts
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

    private final Collection<BuildScriptConfiguratorFactory> configuratorFactories
    @Nullable
    private final File buildScript
    private final Collection<File> buildSrcDirs
    private final Map<String, Object> scriptArgs
    private final Collection<Build> builds = []

    private boolean ranBuildScript = false

    BuildScriptBasedStaticSiteGenerator(
            Collection<BuildScriptConfiguratorFactory> configuratorFactories = [],
            @Nullable File buildScript = null,
            Collection<File> buildSrcDirs = [],
            Map<String, Object> scriptArgs = [:]
    ) {
        this.configuratorFactories = configuratorFactories
        this.buildScript = buildScript
        this.buildSrcDirs = buildSrcDirs
        this.scriptArgs = scriptArgs
    }

    private void runBuildScript() {
        logger.trace(enter, '')

        if (this.buildScript == null) {
            logger.info('no specified build script; using defaults')
            def result = BuildScripts.runBuildScript { base ->
                this.configuratorFactories.each {
                    it.get().accept(base)
                }
            }
            this.builds.addAll(result)
        } else if (this.buildScript.exists() && this.buildScript.isFile()) {
            logger.info('running buildScript: {}', this.buildScript)
            def result = BuildScripts.runBuildScript(
                    this.buildScript.name,
                    this.buildScript.parentFile.toURI().toURL(),
                    this.buildSrcDirs.collect { it.toURI().toURL() },
                    [args: this.scriptArgs]
            ) { base ->
                this.configuratorFactories.each {
                    it.get().accept(base)
                }
            }
            this.builds.addAll(result)
        } else {
            throw new IllegalArgumentException("given buildScript ${ this.buildScript } either does not exist or is not a file")
        }
        this.ranBuildScript = true

        logger.trace(exit, '')
    }

    @Override
    boolean doBuild(String buildName, Consumer<Collection<Diagnostic>> diagnosticsConsumer = { }) {
        logger.trace(enter, 'buildName: {}, diagnosticsConsumer: {}', buildName, diagnosticsConsumer)

        if (!this.ranBuildScript) {
            this.runBuildScript()
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
