package com.jessebrault.ssg.task

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

import java.util.function.Consumer
import java.util.function.Supplier

@TupleConstructor(defaults = false)
@NullCheck(includeGenerated = true)
@EqualsAndHashCode
final class TaskFactorySpec<T extends TaskFactory> {

    static <T extends TaskFactory> TaskFactorySpec<T> concat(TaskFactorySpec<T> spec0, TaskFactorySpec<T> spec1) {
        if (spec0.supplier != spec1.supplier) {
            throw new IllegalArgumentException("suppliers must be equal!")
        }
        new TaskFactorySpec(spec0.supplier, spec0.configurators + spec1.configurators)
    }

    final Supplier<T> supplier
    final Collection<Consumer<T>> configurators

    TaskFactorySpec plus(TaskFactorySpec other) {
        concat(this, other)
    }

}
