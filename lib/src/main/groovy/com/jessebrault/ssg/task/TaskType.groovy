package com.jessebrault.ssg.task

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@TupleConstructor(defaults = false)
@NullCheck(includeGenerated = true)
@EqualsAndHashCode
final class TaskType<T extends Task> {

    final String name
    final TaskExecutor<T> executor

    @Override
    String toString() {
        "TaskType(${ this.name }, ${ this.executor })"
    }

}
