package com.jessebrault.ssg.task

import com.jessebrault.ssg.util.Result
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.PackageScope
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType

@PackageScope
@NullCheck
@EqualsAndHashCode(includeFields = true)
final class ClosureBasedTaskFactory implements TaskFactory {

    private final Closure<Collection<Task>> closure

    ClosureBasedTaskFactory(
            @ClosureParams(value = SimpleType, options = 'com.jessebrault.ssg.task.TaskSpec')
            Closure<Collection<Task>> closure
    ) {
        this.closure = closure
    }

    @Override
    Result<Collection<Task>> getTasks(TaskSpec taskSpec) {
        this.closure(taskSpec)
    }

}
