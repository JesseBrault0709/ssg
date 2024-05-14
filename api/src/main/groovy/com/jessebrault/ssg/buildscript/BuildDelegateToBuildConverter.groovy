package com.jessebrault.ssg.buildscript

import com.jessebrault.ssg.buildscript.delegates.BuildDelegate
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@NullCheck
@TupleConstructor(includeFields = true)
class BuildDelegateToBuildConverter {

    protected Build getFromDelegate(String name, BuildDelegate delegate) {
        new Build(
                name: name,
                siteName: delegate.siteName,
                baseUrl: delegate.baseUrl,
                outputDir: delegate.outputDir,
                globals: delegate.globals,
                models: delegate.models
        )
    }

    protected void getFromBuild(Build source) {
        new BuildDelegate().tap {
            siteName(source.siteName)
            baseUrl(source.siteName)
            outputDir(source.outputDir)
            globals.set(source.globals)
            textsDirs(source.textsDirs)
            models(source.models)
        }
    }

    private final FileBuildScriptGetter buildScriptGetter
    private final File buildScriptsDir

    Build convert(String name, BuildScriptBase buildScript) {
        final BuildDelegate delegate
        if (buildScript.extending != null) {
            def extendingFrom = buildScriptGetter.getBuildInfo(buildScript.extending)
            def build = this.convert(buildScript.extending, extendingFrom)
            delegate = this.getFromBuild(build)
        } else {
            delegate = new BuildDelegate()
        }
        def cl = buildScript.buildClosure
        cl.delegate = delegate
        cl()
        this.getFromDelegate(name, delegate)
    }

}
