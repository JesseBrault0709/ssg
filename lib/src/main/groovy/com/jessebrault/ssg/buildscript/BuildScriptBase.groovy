package com.jessebrault.ssg.buildscript

import com.jessebrault.ssg.Config
import groovy.transform.TupleConstructor

abstract class BuildScriptBase extends Script {

    @TupleConstructor(includeFields = true, defaults = false)
    static class GlobalsConfigurator {

        private final Map globals

        @Override
        Object getProperty(String propertyName) {
            this.globals[propertyName]
        }

        @Override
        void setProperty(String propertyName, Object newValue) {
            this.globals.put(propertyName, newValue)
        }

    }

    Config config
    Map globals

    void config(
            @DelegatesTo(value = Config, strategy = Closure.DELEGATE_FIRST)
            Closure configClosure
    ) {
        configClosure.setDelegate(this.config)
        configClosure.setResolveStrategy(Closure.DELEGATE_FIRST)
        configClosure.run()
    }

    void globals(
            @DelegatesTo(value = GlobalsConfigurator, strategy = Closure.DELEGATE_FIRST)
            Closure globalsClosure
    ) {
        def globalsConfigurator = new GlobalsConfigurator(this.globals)
        globalsClosure.setDelegate(globalsConfigurator)
        globalsClosure.setResolveStrategy(Closure.DELEGATE_FIRST)
        globalsClosure.run()
    }

}
