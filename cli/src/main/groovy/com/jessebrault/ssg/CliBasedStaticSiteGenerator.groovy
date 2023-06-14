package com.jessebrault.ssg

import com.jessebrault.ssg.buildscript.BuildScriptConfiguratorFactory
import com.jessebrault.ssg.util.Diagnostic
import groovy.transform.PackageScope

import java.util.function.Consumer

@PackageScope
final class CliBasedStaticSiteGenerator implements StaticSiteGenerator {

    private final File buildScript
    private final Collection<URL> buildScriptClassLoaderUrls

    private BuildScriptBasedStaticSiteGenerator delegate

    /**
     * @param buildScript The buildScript File.
     * @param buildScriptClassLoaderUrls all the necessary urls to needed to run the given buildScript.
     *          Likely includes the parent directory of the buildScript, as well as buildSrc dir(s).
     */
    CliBasedStaticSiteGenerator(
            File buildScript,
            Collection<URL> buildScriptClassLoaderUrls
    ) {
        this.buildScript = buildScript
        this.buildScriptClassLoaderUrls = buildScriptClassLoaderUrls
    }

    @Override
    boolean doBuild(
            String buildName,
            Collection<BuildScriptConfiguratorFactory> configuratorFactories,
            Map<String, Object> buildScriptArgs,
            Consumer<Collection<Diagnostic>> diagnosticsConsumer
    ) {
        if (this.delegate == null) {
            this.delegate = new BuildScriptBasedStaticSiteGenerator(
                    this.buildScriptClassLoaderUrls,
                    this.buildScript
            )
        }

        this.delegate.doBuild(buildName, configuratorFactories, buildScriptArgs, diagnosticsConsumer)
    }

    GroovyClassLoader getBuildScriptClassLoader() {
        this.delegate.buildScriptClassLoader
    }

}
