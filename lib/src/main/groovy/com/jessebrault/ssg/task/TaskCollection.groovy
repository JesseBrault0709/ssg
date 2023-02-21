package com.jessebrault.ssg.task

import static java.util.Objects.requireNonNull

class TaskCollection<T extends Task> {

    @Delegate
    private final Collection<T> tasks = new ArrayList<T>()

    TaskCollection(Collection<? extends T> tasks = null) {
        if (tasks != null) {
            this.tasks.addAll(requireNonNull(tasks))
        }
    }

    def <U extends T> TaskCollection<U> findAllByType(
            TaskType<U> taskType
    ) {
        new TaskCollection<>(this.tasks.findResults {
            it.type == taskType ? it : null
        })
    }

}
