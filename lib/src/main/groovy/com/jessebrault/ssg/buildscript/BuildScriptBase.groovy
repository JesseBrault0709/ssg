package com.jessebrault.ssg.buildscript

import com.jessebrault.ssg.Build
import com.jessebrault.ssg.Config
import com.jessebrault.ssg.SiteSpec

abstract class BuildScriptBase extends Script {

    Config defaultConfig
    SiteSpec defaultSiteSpec
    Map defaultGlobals

    Collection<Build> builds = []

    protected int currentBuildNumber = 0

    void build(
            @DelegatesTo(value = BuildClosureDelegate, strategy = Closure.DELEGATE_FIRST)
            Closure buildClosure
    ) {
        def buildClosureDelegate = new BuildClosureDelegate().tap {
            // Default values for Build properties
            name = 'build' + this.currentBuildNumber
            config = new Config(defaultConfig)
            siteSpec = new SiteSpec(defaultSiteSpec)
            globals = new LinkedHashMap(defaultGlobals)
            outDir = new File(name)
        }
        buildClosure.setDelegate(buildClosureDelegate)
        buildClosure.setResolveStrategy(Closure.DELEGATE_FIRST)
        buildClosure.run()
        this.builds << new Build(
                buildClosureDelegate.name,
                buildClosureDelegate.config,
                buildClosureDelegate.siteSpec,
                buildClosureDelegate.globals,
                buildClosureDelegate.outDir
        )
        this.currentBuildNumber++
    }

}
