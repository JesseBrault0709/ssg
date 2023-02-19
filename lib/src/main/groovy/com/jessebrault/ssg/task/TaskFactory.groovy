package com.jessebrault.ssg.task

import org.jetbrains.annotations.Nullable

interface TaskFactory {
    @Nullable Task getTask(Input input, TaskContext context)
}