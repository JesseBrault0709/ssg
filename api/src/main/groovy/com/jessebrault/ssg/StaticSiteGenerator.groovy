package com.jessebrault.ssg

import com.jessebrault.ssg.buildscript.BuildScriptConfiguratorFactory
import com.jessebrault.ssg.util.Diagnostic

import java.util.function.Consumer

interface StaticSiteGenerator {
    boolean doBuild(
            String buildName,
            Collection<BuildScriptConfiguratorFactory> configuratorFactories,
            Map<String, Object> buildScriptArgs,
            Consumer<Collection<Diagnostic>> diagnosticsConsumer
    )
}