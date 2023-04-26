package com.jessebrault.ssg.buildscript.dsl

import com.jessebrault.ssg.task.TaskFactory
import com.jessebrault.ssg.task.TaskFactorySpec

import java.util.function.Consumer
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
            Consumer<T> factoryConfigurator
    ) {
        this.checkNotRegistered(name)
        this.specs[name] = new TaskFactorySpec(factorySupplier, [factoryConfigurator as Closure<?>])
    }

    void configure(String name, Consumer<? extends TaskFactory> factoryConfigureClosure) {
        if (!this.specs.containsKey(name)) {
            throw new IllegalArgumentException("there is no TaskFactory registered by name ${ name }")
        }
        this.specs[name].configureClosures << (factoryConfigureClosure as Closure<Void>)
    }

    Map<String, TaskFactorySpec> getResult() {
        this.specs
    }

}
