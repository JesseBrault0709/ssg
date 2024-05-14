package com.jessebrault.ssg.buildscript

import com.jessebrault.ssg.buildscript.delegates.BuildDelegate
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@NullCheck
@TupleConstructor(includeFields = true)
class BuildDelegateToBuildSpecConverter {

    private final FileBuildScriptGetter buildScriptGetter

    protected BuildSpec getFromDelegate(String name, BuildDelegate delegate) {
        new BuildSpec(
                name: name,
                siteName: delegate.siteName,
                baseUrl: delegate.baseUrl,
                outputDir: delegate.outputDir,
                globals: delegate.globals,
                textsDirs: delegate.textsDirs,
                models: delegate.models
        )
    }

    BuildSpec convert(final String name, final BuildScriptBase buildScript) {
        final Deque<BuildScriptBase> buildHierarchy = new LinkedList<>()
        buildHierarchy.push(buildScript)
        String extending = buildScript.extending
        while (extending != null) {
            def from = this.buildScriptGetter.getBuildScript(extending)
            buildHierarchy.push(from)
            extending = from.extending
        }

        def delegate = new BuildDelegate()
        while (!buildHierarchy.isEmpty()) {
            def currentScript = buildHierarchy.pop()
            currentScript.buildClosure.delegate = delegate
            currentScript.buildClosure()
        }

        this.getFromDelegate(name, delegate)
    }

}
