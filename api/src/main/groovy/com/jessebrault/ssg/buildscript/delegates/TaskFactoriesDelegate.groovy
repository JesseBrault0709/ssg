package com.jessebrault.ssg.buildscript.delegates

import com.jessebrault.ssg.task.TaskFactory
import com.jessebrault.ssg.task.TaskFactorySpec
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck

import java.util.function.Consumer
import java.util.function.Supplier

@NullCheck
@EqualsAndHashCode(includeFields = true)
final class TaskFactoriesDelegate {

    private final Collection<TaskFactorySpec<TaskFactory>> specs = []

    private boolean isRegistered(String name) {
        this.specs.find { it.name == name } != null
    }

    private void checkNotRegistered(String name) {
        if (this.isRegistered(name)) {
            throw new IllegalArgumentException("a TaskFactory is already registered by the name ${ name }")
        }
    }

    void register(String name, Supplier<? extends TaskFactory> factorySupplier) {
        this.checkNotRegistered(name)
        this.specs << new TaskFactorySpec<>(name, factorySupplier, [])
    }

    def <T extends TaskFactory> void register(
            String name,
            Supplier<T> factorySupplier,
            Consumer<T> factoryConfigurator
    ) {
        this.checkNotRegistered(name)
        this.specs << new TaskFactorySpec<>(name, factorySupplier, [factoryConfigurator])
    }

    void register(TaskFactorySpec<TaskFactory> spec) {
        this.specs << spec
    }

    void registerAll(Collection<TaskFactorySpec<TaskFactory>> specs) {
        this.specs.addAll(specs)
    }

    def <T extends TaskFactory> void configure(
            String name,
            Class<T> factoryClass, // Dummy so we get better auto-complete
            Consumer<T> factoryConfigurator
    ) {
        if (!this.isRegistered(name)) {
            throw new IllegalArgumentException("there is no TaskFactory registered by name ${ name }")
        }
        def spec = this.specs.find { it.name == name }
        // Potentially dangerous, but the configurators Collection *should* only contain the correct types.
        spec.configurators << (factoryConfigurator as Consumer<TaskFactory>)
    }

    Collection<TaskFactorySpec<TaskFactory>> getResult() {
        this.specs
    }

}
