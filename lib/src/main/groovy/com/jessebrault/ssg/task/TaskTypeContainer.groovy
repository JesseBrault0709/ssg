package com.jessebrault.ssg.task

final class TaskTypeContainer {

    @Delegate
    private final Set<TaskType<? extends Task>> taskTypes = []

    TaskTypeContainer(Collection<TaskType<? extends Task>> taskTypes) {
        if (taskTypes != null) {
            this.taskTypes.addAll(taskTypes)
        }
    }

    TaskTypeContainer(TaskTypeContainer taskTypeContainer) {
        if (taskTypeContainer != null) {
            this.taskTypes.addAll(taskTypeContainer)
        }
    }

    TaskTypeContainer() {}

    @Override
    TaskType<? extends Task> getProperty(String propertyName) {
        def taskType = this.taskTypes.find { it.name == propertyName }
        if (!taskType) {
            throw new IllegalArgumentException("no such taskType: ${ propertyName }")
        }
        taskType
    }

}
