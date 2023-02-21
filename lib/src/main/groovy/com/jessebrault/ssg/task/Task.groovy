package com.jessebrault.ssg.task

interface Task {
    String getName()
    void execute(TaskExecutorContext context)
}