package com.jessebrault.ssg.task

import com.jessebrault.ssg.Build
import com.jessebrault.ssg.Result

interface TaskFactory<T extends Task> {
    Result<TaskCollection<T>> getTasks(Build build)
}