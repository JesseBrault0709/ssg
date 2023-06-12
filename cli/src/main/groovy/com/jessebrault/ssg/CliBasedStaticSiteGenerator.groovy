package com.jessebrault.ssg

import com.jessebrault.ssg.buildscript.DefaultBuildScriptConfiguratorFactory
import com.jessebrault.ssg.util.Diagnostic
import groovy.transform.PackageScope

import java.util.function.Consumer

@PackageScope
final class CliBasedStaticSiteGenerator implements StaticSiteGenerator {

    private final File baseDir
    private final File buildScript
    private final File tmpDir
    private final Map<String, String> scriptArgs
    private final GroovyScriptEngine engine

    private StaticSiteGenerator staticSiteGenerator

    CliBasedStaticSiteGenerator(
            File baseDir,
            File buildScript,
            File tmpDir,
            GroovyScriptEngine engine,
            Map<String, String> scriptArgs
    ) {
        this.baseDir = baseDir
        this.buildScript = buildScript
        this.tmpDir = tmpDir
        this.scriptArgs = scriptArgs
        this.engine = engine
    }

    @Override
    boolean doBuild(String buildName, Consumer<Collection<Diagnostic>> diagnosticsConsumer) {
        if (this.staticSiteGenerator == null) {
            this.staticSiteGenerator = new BuildScriptBasedStaticSiteGenerator(
                    this.engine,
                    [new DefaultBuildScriptConfiguratorFactory(this.baseDir, this.tmpDir, this.engine)],
                    this.buildScript,
                    this.scriptArgs
            )
        }

        this.staticSiteGenerator.doBuild(buildName, diagnosticsConsumer)
    }

}
