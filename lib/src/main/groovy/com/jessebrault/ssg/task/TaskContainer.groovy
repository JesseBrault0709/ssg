package com.jessebrault.ssg.task

final class TaskContainer extends TaskCollection<Task> {

    TaskContainer(Collection<? extends Task> tasks = null) {
        super(tasks)
    }

}
