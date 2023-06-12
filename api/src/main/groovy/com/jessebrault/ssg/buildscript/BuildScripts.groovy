package com.jessebrault.ssg.buildscript

import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType
import org.codehaus.groovy.control.CompilerConfiguration

import java.util.function.Consumer

final class BuildScripts {

    private static Collection<Build> runBase(BuildScriptBase base) {
        base.run()
        BuildSpecUtil.getBuilds(base.getBuildSpecs())
    }

    static Collection<Build> runBuildScript(
            @DelegatesTo(value = BuildScriptBase, strategy = Closure.DELEGATE_FIRST)
            @ClosureParams(value = SimpleType, options = 'com.jessebrault.ssg.buildscript.BuildScriptBase')
            Closure<?> scriptBody
    ) {
        def base = new BuildScriptBase() {

            @Override
            Object run() {
                scriptBody.delegate = this
                scriptBody.resolveStrategy = Closure.DELEGATE_FIRST
                scriptBody.call(this)
            }

        }
        runBase(base)
    }

    static Collection<Build> runBuildScript(
            String scriptName,
            GroovyScriptEngine engine,
            Map<String, Object> binding = [:],
            Consumer<BuildScriptBase> configureBuildScript = { }
    ) {
        engine.config = new CompilerConfiguration().tap {
            scriptBaseClass = 'com.jessebrault.ssg.buildscript.BuildScriptBase'
        }

        def base = engine.createScript(scriptName, new Binding(binding))
        assert base instanceof BuildScriptBase
        configureBuildScript.accept(base)
        runBase(base)
    }

    @Deprecated
    static Collection<Build> runBuildScript(
            String scriptName,
            URL scriptBaseDirUrl,
            Collection<URL> otherUrls,
            Map<String, Object> binding,
            Consumer<BuildScriptBase> configureBuildScript
    ) {
        def engine = new GroovyScriptEngine([scriptBaseDirUrl, *otherUrls] as URL[])

        engine.config = new CompilerConfiguration().tap {
            scriptBaseClass = 'com.jessebrault.ssg.buildscript.BuildScriptBase'
        }

        def base = engine.createScript(scriptName, new Binding(binding))
        assert base instanceof BuildScriptBase
        configureBuildScript.accept(base)
        runBase(base)
    }

    @Deprecated
    static Collection<Build> runBuildScript(
            String scriptName,
            URL scriptBaseDirUrl,
            Collection<URL> otherUrls,
            Map<String, Object> binding
    ) {
        runBuildScript(scriptName, scriptBaseDirUrl, otherUrls, binding) { }
    }

    @Deprecated
    static Collection<Build> runBuildScript(
            String scriptName,
            URL scriptBaseDirUrl,
            Collection<URL> otherUrls
    ) {
        runBuildScript(scriptName, scriptBaseDirUrl, otherUrls, [:]) { }
    }

    @Deprecated
    static Collection<Build> runBuildScript(
            String scriptName,
            URL scriptBaseDirUrl
    ) {
        runBuildScript(scriptName, scriptBaseDirUrl, [], [:]) { }
    }

    private BuildScripts() {}

}
