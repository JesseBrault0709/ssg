package com.jessebrault.ssg.buildscript

import com.jessebrault.ssg.Config
import org.codehaus.groovy.control.CompilerConfiguration

class GroovyBuildScriptRunner implements BuildScriptRunner {

    @Override
    void runBuildScript(Config config, Map globals) {
        Objects.requireNonNull(config)
        Objects.requireNonNull(globals)
        def engine = new GroovyScriptEngine([new File('.').toURI().toURL()] as URL[])
        engine.config = new CompilerConfiguration().tap {
            scriptBaseClass = 'com.jessebrault.ssg.buildscript.BuildScriptBase'
        }
        def buildScript = engine.createScript('build.groovy', new Binding())
        assert buildScript instanceof BuildScriptBase
        buildScript.config = config
        buildScript.globals = globals
        buildScript.run()
    }

}
