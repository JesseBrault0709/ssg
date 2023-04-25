package com.jessebrault.ssg.buildscript.dsl

import com.jessebrault.ssg.task.TaskFactory
import com.jessebrault.ssg.task.TaskFactorySpec
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SecondParam

import java.util.function.Supplier

final class TaskFactoriesDelegate {

    private final Map<String, TaskFactorySpec> specs = [:]

    private void checkNotRegistered(String name) {
        if (this.specs.containsKey(name)) {
            throw new IllegalArgumentException("a TaskFactory is already registered by the name ${ name }")
        }
    }

    void register(String name, Supplier<? extends TaskFactory> factorySupplier) {
        this.checkNotRegistered(name)
        this.specs[name] = new TaskFactorySpec(factorySupplier, [])
    }

    def <T extends TaskFactory> void register(
            String name,
            Supplier<T> factorySupplier,
            @ClosureParams(value = SecondParam.FirstGenericType)
            Closure<Void> factoryConfigureClosure
    ) {
        this.checkNotRegistered(name)
        this.specs[name] = new TaskFactorySpec(factorySupplier, [factoryConfigureClosure])
    }

    void configure(String name, Closure<Void> factoryConfigureClosure) {
        if (!this.specs.containsKey(name)) {
            throw new IllegalArgumentException("there is no TaskFactory registered by name ${ name }")
        }
        this.specs[name].configureClosures << factoryConfigureClosure
    }

    Map<String, TaskFactorySpec> getResult() {
        this.specs
    }

}
