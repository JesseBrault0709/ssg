package com.jessebrault.ssg.buildscript

import com.jessebrault.ssg.Config

class BuildClosureDelegate {

    String name
    Config config
    Map globals
    File outDir

    void config(
            @DelegatesTo(value = ConfigClosureDelegate, strategy = Closure.DELEGATE_FIRST)
                    Closure configClosure
    ) {
        configClosure.setDelegate(new ConfigClosureDelegate(this.config))
        configClosure.setResolveStrategy(Closure.DELEGATE_FIRST)
        configClosure.run()
    }

    void globals(
            @DelegatesTo(value = GlobalsClosureDelegate, strategy = Closure.DELEGATE_FIRST)
                    Closure globalsClosure
    ) {
        def globalsConfigurator = new GlobalsClosureDelegate(this.globals)
        globalsClosure.setDelegate(globalsConfigurator)
        globalsClosure.setResolveStrategy(Closure.DELEGATE_FIRST)
        globalsClosure.run()
    }

}
