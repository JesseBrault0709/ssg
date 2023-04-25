package com.jessebrault.ssg

import com.jessebrault.ssg.buildscript.Build
import com.jessebrault.ssg.task.Task
import com.jessebrault.ssg.util.Result

interface BuildTasksConverter {
    Result<Collection<Task>> convert(Build buildScriptResult)
}
