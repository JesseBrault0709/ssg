package com.jessebrault.ssg.buildscript

import groovy.transform.NullCheck
import org.codehaus.groovy.control.CompilerConfiguration

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
        buildScript.getBuilds()
    }

}
