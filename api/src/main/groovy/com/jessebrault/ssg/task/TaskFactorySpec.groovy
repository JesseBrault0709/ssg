package com.jessebrault.ssg.task

import com.jessebrault.ssg.util.Eq
import com.jessebrault.ssg.util.Eqs
import com.jessebrault.ssg.util.Semigroup
import com.jessebrault.ssg.util.Semigroups
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

import java.util.function.Consumer
import java.util.function.Supplier

/**
 * TODO: Make this deeply immutable
 */
@TupleConstructor(defaults = false)
@NullCheck(includeGenerated = true)
@EqualsAndHashCode
final class TaskFactorySpec<T extends TaskFactory> {

    static final Semigroup<TaskFactorySpec<TaskFactory>> DEFAULT_SEMIGROUP = Semigroups.of(TaskFactorySpec::concat)

    static final Eq<TaskFactorySpec<TaskFactory>> SAME_NAME_AND_SUPPLIER_EQ = Eqs.of(TaskFactorySpec::areSame)

    static <T extends TaskFactory> TaskFactorySpec<T> concat(TaskFactorySpec<T> spec0, TaskFactorySpec<T> spec1) {
        if (spec0.name != spec1.name) {
            throw new IllegalArgumentException('names must be equal!')
        }
        if (spec0.supplier != spec1.supplier) {
            throw new IllegalArgumentException("suppliers must be equal!")
        }
        new TaskFactorySpec(spec0.name, spec0.supplier, spec0.configurators + spec1.configurators)
    }

    static boolean areSame(TaskFactorySpec<TaskFactory> spec0, TaskFactorySpec<TaskFactory> spec1) {
        spec0.name == spec1.name && spec0.supplier == spec1.supplier
    }

    final String name
    final Supplier<T> supplier
    final Collection<Consumer<T>> configurators

    TaskFactorySpec plus(TaskFactorySpec other) {
        concat(this, other)
    }

    @Override
    String toString() {
        "TaskFactorySpec(name: ${ this.name })"
    }

}
