package com.jessebrault.ssg

import com.jessebrault.ssg.buildscript.DefaultBuildScriptConfiguratorFactory
import com.jessebrault.ssg.util.Diagnostic
import groovy.transform.PackageScope

import java.util.function.Consumer

@PackageScope
final class CliBasedStaticSiteGenerator implements StaticSiteGenerator {

    private final File baseDir
    private final File buildScript
    private final Collection<File> buildSrcDirs
    private final Map<String, String> scriptArgs
    private final ClassLoader classLoader
    private final Collection<URL> scriptBaseUrls

    private StaticSiteGenerator staticSiteGenerator

    CliBasedStaticSiteGenerator(
            File baseDir,
            File buildScript,
            Collection<File> buildSrcDirs,
            Map<String, String> scriptArgs,
            ClassLoader classLoader,
            Collection<URL> scriptBaseUrls
    ) {
        this.baseDir = baseDir
        this.buildScript = buildScript
        this.buildSrcDirs = buildSrcDirs
        this.scriptArgs = scriptArgs
        this.classLoader = classLoader
        this.scriptBaseUrls = scriptBaseUrls
    }

    @Override
    boolean doBuild(String buildName, Consumer<Collection<Diagnostic>> diagnosticsConsumer) {
        if (this.staticSiteGenerator == null) {
            this.staticSiteGenerator = new BuildScriptBasedStaticSiteGenerator(
                    [new DefaultBuildScriptConfiguratorFactory(this.baseDir, this.classLoader, this.scriptBaseUrls)],
                    this.buildScript == new File('ssgBuilds.groovy') || this.buildScript.exists()
                            ? new File(this.baseDir, this.buildScript.path)
                            : null,
                    this.buildSrcDirs.collect { new File(this.baseDir, it.path) },
                    this.scriptArgs
            )
        }

        this.staticSiteGenerator.doBuild(buildName, diagnosticsConsumer)
    }

}
