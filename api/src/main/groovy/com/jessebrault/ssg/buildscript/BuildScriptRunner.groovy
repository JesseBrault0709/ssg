package com.jessebrault.ssg.buildscript

import java.util.function.Consumer

interface BuildScriptRunner {

    Collection<Build> runBuildScript(
            String scriptName,
            URL scriptBaseDirUrl,
            Collection<URL> otherUrls,
            Consumer<BuildScriptBase> configureBuildScript
    )

    default Collection<Build> runBuildScript(
            String scriptName,
            URL scriptBaseDirUrl,
            Collection<URL> otherUrls
    ) {
        this.runBuildScript(scriptName, scriptBaseDirUrl, otherUrls) { }
    }

    default Collection<Build> runBuildScript(
            String scriptName,
            URL scriptBaseDirUrl
    ) {
        this.runBuildScript(scriptName, scriptBaseDirUrl, [])
    }

}
