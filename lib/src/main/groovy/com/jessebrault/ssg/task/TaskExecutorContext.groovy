package com.jessebrault.ssg.task

import com.jessebrault.ssg.Build
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@TupleConstructor(defaults = false)
@NullCheck(includeGenerated = true)
@EqualsAndHashCode
final class TaskExecutorContext {

    final Build build
    final TaskContainer allTasks
    final TaskTypeContainer allTypes
    final Closure onDiagnostics

    @Override
    String toString() {
        "TaskExecutorContext(build: ${ this.build }, allTasks: ${ this.allTasks }, allTypes: ${ this.allTypes })"
    }

}
