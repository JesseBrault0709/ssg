package com.jessebrault.ssg.task

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

import java.util.function.Supplier

@TupleConstructor(defaults = false)
@NullCheck(includeGenerated = true)
@EqualsAndHashCode
final class TaskFactorySpec {

    static TaskFactorySpec concat(TaskFactorySpec spec0, TaskFactorySpec spec1) {
        if (spec0.supplier != spec1.supplier) {
            throw new IllegalArgumentException("suppliers must be equal!")
        }
        new TaskFactorySpec(spec0.supplier, spec0.configureClosures + spec1.configureClosures)
    }

    final Supplier<TaskFactory> supplier
    final Collection<Closure<Void>> configureClosures

    TaskFactorySpec plus(TaskFactorySpec other) {
        concat(this, other)
    }

}
