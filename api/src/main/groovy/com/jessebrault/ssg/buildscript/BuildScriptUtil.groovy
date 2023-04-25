package com.jessebrault.ssg.buildscript

import groovy.transform.NullCheck
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer

import java.util.function.Consumer

@NullCheck
final class BuildScriptUtil {

    // TODO: check exactly what we are importing to the script automatically
    // TODO: check the roots arg, do we include 'ssgBuilds'/'buildSrc' dir eventually?
    static Collection<Build> runBuildScript(String relativePath, Consumer<BuildScriptBase> configureBuildScript) {
        def engine = new GroovyScriptEngine([new File('.').toURI().toURL()] as URL[])
        engine.config = new CompilerConfiguration().tap {
            addCompilationCustomizers(new ImportCustomizer().tap {
                addStarImports(
                        'com.jessebrault.ssg',
                        'com.jessebrault.ssg.part',
                        'com.jessebrault.ssg.page',
                        'com.jessebrault.ssg.template',
                        'com.jessebrault.ssg.text',
                        'com.jessebrault.ssg.util'
                )
            })
            scriptBaseClass = 'com.jessebrault.ssg.buildscript.BuildScriptBase'
        }

        def buildScript = engine.createScript(relativePath, new Binding())
        assert buildScript instanceof BuildScriptBase
        configureBuildScript.accept(buildScript)
        buildScript()
        buildScript.getBuilds()
    }

    private BuildScriptUtil() {}

}
