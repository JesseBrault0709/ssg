package com.jessebrault.ssg.task

import com.jessebrault.ssg.util.Result

interface TaskFactory {
    Result<Collection<Task>> getTasks(TaskSpec taskSpec)
}
