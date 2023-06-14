package com.jessebrault.ssg.buildscript

import com.jessebrault.ssg.util.ExtensionUtil
import groovy.transform.NullCheck
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType
import org.codehaus.groovy.control.CompilerConfiguration

import java.util.function.Consumer

@NullCheck
final class BuildScriptRunner {

    private static Collection<Build> runBase(BuildScriptBase base) {
        base.run()
        BuildSpecUtil.getBuilds(base.getBuildSpecs())
    }

    static Collection<Build> runClosureScript(
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

    private final GroovyClassLoader buildScriptClassLoader

    BuildScriptRunner(Collection<URL> classLoaderUrls) {
        this.buildScriptClassLoader = new GroovyClassLoader(
                Thread.currentThread().contextClassLoader,
                new CompilerConfiguration().tap {
                    scriptBaseClass = BuildScriptBase.name
                }
        )
        classLoaderUrls.each(this.buildScriptClassLoader::addURL)
    }

    GroovyClassLoader getBuildScriptClassLoader() {
        this.buildScriptClassLoader
    }

    Collection<Build> runBuildScript(
            String scriptName,
            Map<String, Object> binding,
            Consumer<BuildScriptBase> configureBuildScript
    ) {
        Class<?> scriptClass = this.buildScriptClassLoader.loadClass(
                ExtensionUtil.stripExtension(scriptName),
                true,
                false,
                false
        )
        def scriptObject = scriptClass.getConstructor().newInstance()
        assert scriptObject instanceof BuildScriptBase
        scriptObject.setBinding(new Binding(binding))
        configureBuildScript.accept(scriptObject)
        runBase(scriptObject)
    }

}
