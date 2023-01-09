package com.jessebrault.ssg.buildscript

import com.jessebrault.ssg.Build
import com.jessebrault.ssg.Config

abstract class BuildScriptBase extends Script {

    Config defaultConfig
    Map defaultGlobals

    Collection<Build> builds = []

    private int currentBuildNumber = 0

    void build(
            @DelegatesTo(value = BuildClosureDelegate, strategy = Closure.DELEGATE_FIRST)
            Closure buildClosure
    ) {
        def buildClosureDelegate = new BuildClosureDelegate().tap {
            // Default values for Build properties
            name = 'build' + this.currentBuildNumber
            config = new Config(defaultConfig)
            globals = new LinkedHashMap(defaultGlobals)
            outDir = new File(name)
        }
        buildClosure.setDelegate(buildClosureDelegate)
        buildClosure.setResolveStrategy(Closure.DELEGATE_FIRST)
        buildClosure.run()
        this.builds << new Build(buildClosureDelegate.name, buildClosureDelegate.config, buildClosureDelegate.globals, buildClosureDelegate.outDir)
        this.currentBuildNumber++
    }

}
