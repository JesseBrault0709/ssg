package com.jessebrault.ssg.buildscript

import com.jessebrault.ssg.Build
import com.jessebrault.ssg.Config
import groovy.transform.NullCheck
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer

@NullCheck
class GroovyBuildScriptRunner implements BuildScriptRunner {

    @Override
    Collection<Build> runBuildScript(String relativePath, Config defaultConfig, Map defaultGlobals) {
        def engine = new GroovyScriptEngine([new File('.').toURI().toURL()] as URL[])
        engine.config = new CompilerConfiguration().tap {
            addCompilationCustomizers(new ImportCustomizer().tap {
                addStarImports(
                        'com.jessebrault.ssg',
                        'com.jessebrault.ssg.part',
                        'com.jessebrault.ssg.specialpage',
                        'com.jessebrault.ssg.template',
                        'com.jessebrault.ssg.text',
                        'com.jessebrault.ssg.util'
                )
            })
            scriptBaseClass = 'com.jessebrault.ssg.buildscript.BuildScriptBase'
        }

        def buildScript = engine.createScript(relativePath, new Binding())
        assert buildScript instanceof BuildScriptBase
        buildScript.defaultConfig = defaultConfig
        buildScript.defaultGlobals = defaultGlobals
        buildScript.run()
        buildScript.builds
    }

}
