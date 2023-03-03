package com.jessebrault.ssg.task

interface Task {
    TaskType<? extends Task> getType()
    String getName()
    void execute(TaskExecutorContext context)
}