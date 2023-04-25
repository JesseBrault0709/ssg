package com.jessebrault.ssg.dsl

import com.jessebrault.ssg.task.Task
import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode(includeFields = true)
final class TaskCollection {

    @Delegate
    private final Collection<Task> tasks

    TaskCollection(Collection<? extends Task> src = []) {
        this.tasks = []
        this.tasks.addAll(src)
    }

    def <T extends Task> Collection<T> byType(Class<T> taskClass) {
        this.tasks.findAll { taskClass.isAssignableFrom(it.class) } as Collection<T>
    }

}
