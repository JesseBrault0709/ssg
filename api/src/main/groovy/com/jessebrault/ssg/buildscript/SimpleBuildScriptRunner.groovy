package com.jessebrault.ssg.buildscript

import groovy.transform.NullCheck
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType
import org.codehaus.groovy.control.CompilerConfiguration
import org.jgrapht.traverse.DepthFirstIterator

import java.util.function.Consumer

@NullCheck
final class SimpleBuildScriptRunner implements BuildScriptRunner {

    @Override
    Collection<Build> runBuildScript(
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

        def buildScript = engine.createScript(scriptName, new Binding(binding))
        assert buildScript instanceof BuildScriptBase
        configureBuildScript.accept(buildScript)
        buildScript.run()
        BuildSpecUtil.getBuilds(buildScript.buildSpecs)
    }

    @Override
    Collection<Build> runBuildScript(
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
        base.run()
        BuildSpecUtil.getBuilds(base.buildSpecs)
    }

}
