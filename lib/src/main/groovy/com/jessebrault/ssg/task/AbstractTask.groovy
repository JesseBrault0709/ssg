package com.jessebrault.ssg.task

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck

@NullCheck
@EqualsAndHashCode
abstract class AbstractTask<T extends Task> implements Task {

    final TaskType<T> type
    final String name

    AbstractTask(TaskType<T> type, String name) {
        this.type = type
        this.name = name
    }

    protected abstract T getThis()

    @Override
    void execute(TaskExecutorContext context) {
        // I am guessing that if we put this.getThis(), it will think the runtime type is AbstractTask? Not sure.
        this.type.executor.execute(getThis(), context)
    }

    @Override
    String toString() {
        "AbstractTask(name: ${ this.name }, type: ${ this.type })"
    }

}
