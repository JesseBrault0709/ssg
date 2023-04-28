package com.jessebrault.ssg.buildscript.dsl

import com.jessebrault.ssg.task.TaskFactory
import com.jessebrault.ssg.task.TaskFactorySpec

import java.util.function.Consumer
import java.util.function.Supplier

final class TaskFactoriesDelegate {

    private final Map<String, TaskFactorySpec<TaskFactory>> specs = [:]

    private void checkNotRegistered(String name) {
        if (this.specs.containsKey(name)) {
            throw new IllegalArgumentException("a TaskFactory is already registered by the name ${ name }")
        }
    }

    void register(String name, Supplier<? extends TaskFactory> factorySupplier) {
        this.checkNotRegistered(name)
        this.specs[name] = new TaskFactorySpec<>(factorySupplier, [])
    }

    def <T extends TaskFactory> void register(
            String name,
            Supplier<T> factorySupplier,
            Consumer<T> factoryConfigurator
    ) {
        this.checkNotRegistered(name)
        this.specs[name] = new TaskFactorySpec<>(factorySupplier, [factoryConfigurator])
    }

    def <T extends TaskFactory> void configure(
            String name,
            Class<T> factoryClass, // Dummy so we get better auto-complete
            Consumer<T> factoryConfigureClosure
    ) {
        if (!this.specs.containsKey(name)) {
            throw new IllegalArgumentException("there is no TaskFactory registered by name ${ name }")
        }
        // Potentially dangerous, but the configurators Collection *should* only contain the correct types.
        this.specs[name].configurators << (factoryConfigureClosure as Consumer<TaskFactory>)
    }

    Map<String, TaskFactorySpec> getResult() {
        this.specs
    }

}
