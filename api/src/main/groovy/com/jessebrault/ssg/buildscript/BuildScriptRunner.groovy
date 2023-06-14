package com.jessebrault.ssg.buildscript

import com.jessebrault.ssg.util.ExtensionUtil
import groovy.transform.NullCheck
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType
import org.codehaus.groovy.control.CompilerConfiguration
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.Marker
import org.slf4j.MarkerFactory

import java.util.function.Consumer

@NullCheck
final class BuildScriptRunner {

    private static final Logger logger = LoggerFactory.getLogger(BuildScriptRunner)
    private static final Marker enter = MarkerFactory.getMarker('ENTER')
    private static final Marker exit = MarkerFactory.getMarker('EXIT')

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
        logger.trace(enter, 'scriptName: {}, binding: {}', scriptName, binding)
        Class<?> scriptClass = this.buildScriptClassLoader.loadClass(ExtensionUtil.stripExtension(scriptName))
        def scriptObject = scriptClass.getConstructor().newInstance()
        assert scriptObject instanceof BuildScriptBase
        scriptObject.setBinding(new Binding(binding))
        configureBuildScript.accept(scriptObject)
        def result = runBase(scriptObject)
        logger.trace(exit, 'result: {}', result)
        result
    }

}
