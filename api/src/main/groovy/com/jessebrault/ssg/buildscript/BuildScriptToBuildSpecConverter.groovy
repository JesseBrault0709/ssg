package com.jessebrault.ssg.buildscript

import com.jessebrault.ssg.buildscript.delegates.BuildDelegate
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

import java.util.function.Supplier

@NullCheck
@TupleConstructor(includeFields = true)
class BuildScriptToBuildSpecConverter {

    private final BuildScriptGetter buildScriptGetter
    private final Supplier<BuildDelegate> buildDelegateSupplier

    protected BuildSpec getFromDelegate(String name, BuildDelegate delegate) {
        new BuildSpec(
                name: name,
                basePackages: delegate.basePackages,
                siteName: delegate.siteName,
                baseUrl: delegate.baseUrl,
                outputDir: delegate.outputDir,
                globals: delegate.globals,
                models: delegate.models,
                textsDirs: delegate.textsDirs,
                textConverters: delegate.textConverters,
                objectFactoryBuilder: delegate.objectFactoryBuilder
        )
    }

    protected BuildSpec doConvert(String name, BuildScriptBase buildScript) {
        final Deque<BuildScriptBase> buildHierarchy = new LinkedList<>()
        buildHierarchy.push(buildScript)
        String extending = buildScript.extending
        while (extending != null) {
            def from = this.buildScriptGetter.getAndRunBuildScript(extending)
            buildHierarchy.push(from)
            extending = from.extending
        }

        def delegate = this.buildDelegateSupplier.get()
        while (!buildHierarchy.isEmpty()) {
            def currentScript = buildHierarchy.pop()
            currentScript.buildClosure.delegate = delegate
            currentScript.buildClosure()
        }

        this.getFromDelegate(name, delegate)
    }

    BuildSpec convert(String buildScriptFqn) {
        def start = this.buildScriptGetter.getAndRunBuildScript(buildScriptFqn)
        this.doConvert(buildScriptFqn, start)
    }

}
