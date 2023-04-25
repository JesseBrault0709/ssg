package com.jessebrault.ssg.task

import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType

final class TaskFactories {

    static TaskFactory of(
            @ClosureParams(value = SimpleType, options = 'com.jessebrault.ssg.task.TaskSpec')
            Closure<Collection<Void>> closure
    ) {
        new ClosureBasedTaskFactory(closure)
    }

    private TaskFactories() {}

}
