package com.jessebrault.ssg.buildscript

import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType

import java.util.function.Consumer

/**
 * TODO: get rid of this, split it into two different classes/util functions (BuildScripts?)
 */
interface BuildScriptRunner {

    Collection<Build> runBuildScript(
            String scriptName,
            URL scriptBaseDirUrl,
            Collection<URL> otherUrls,
            Map<String, Object> binding,
            Consumer<BuildScriptBase> configureBuildScript
    )

    Collection<Build> runBuildScript(
            @DelegatesTo(value = BuildScriptBase, strategy = Closure.DELEGATE_FIRST)
            @ClosureParams(value = SimpleType, options = 'com.jessebrault.ssg.buildscript.BuildScriptBase')
            Closure<?> scriptBody
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
