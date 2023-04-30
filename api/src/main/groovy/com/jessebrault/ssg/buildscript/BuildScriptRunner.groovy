package com.jessebrault.ssg.buildscript

import java.util.function.Consumer

interface BuildScriptRunner {

    Collection<Build> runBuildScript(
            String scriptName,
            URL scriptBaseDirUrl,
            Collection<URL> otherUrls,
            Map<String, Object> binding,
            Consumer<BuildScriptBase> configureBuildScript
    )

    default Collection<Build> runBuildScript(
            String scriptName,
            URL scriptBaseDirUrl,
            Collection<URL> otherUrls,
            Map<String, Object> binding
    ) {
        this.runBuildScript(scriptName, scriptBaseDirUrl, otherUrls, binding) { }
    }

    default Collection<Build> runBuildScript(
            String scriptName,
            URL scriptBaseDirUrl,
            Collection<URL> otherUrls
    ) {
        this.runBuildScript(scriptName, scriptBaseDirUrl, otherUrls, [:]) { }
    }

    default Collection<Build> runBuildScript(
            String scriptName,
            URL scriptBaseDirUrl
    ) {
        this.runBuildScript(scriptName, scriptBaseDirUrl, [], [:]) { }
    }

}
