package com.jessebrault.ssg.buildscript

import groovy.transform.NullCheck
import groovy.transform.TupleConstructor
import org.codehaus.groovy.control.CompilerConfiguration

@NullCheck(includeGenerated = true)
@TupleConstructor(includeFields = true, defaults = false)
final class BuildScriptGetter {

    private final GroovyClassLoader groovyClassLoader
    private final URL[] scriptBaseUrls
    private final Map<String, String> scriptCliArgs
    private final File projectDir

    BuildScriptBase getAndRunBuildScript(String fqn) {
        def gcl = new GroovyClassLoader(this.groovyClassLoader, new CompilerConfiguration().tap {
            it.scriptBaseClass = BuildScriptBase.name
        })
        this.scriptBaseUrls.each { gcl.addURL(it) }
        def scriptClass = gcl.loadClass(fqn, true, true) as Class<BuildScriptBase>
        def buildScript = scriptClass.getConstructor().newInstance()
        buildScript.binding = new Binding(this.scriptCliArgs)
        buildScript.projectRoot = projectDir
        buildScript.buildName = fqn
        buildScript.run()
        buildScript
    }

}
