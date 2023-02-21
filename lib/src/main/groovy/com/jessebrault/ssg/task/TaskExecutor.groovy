package com.jessebrault.ssg.task

interface TaskExecutor<T extends Task> {
    void execute(T task, TaskExecutorContext context)
}