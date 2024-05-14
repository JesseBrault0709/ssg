package com.jessebrault.ssg

import com.jessebrault.ssg.buildscript.BuildSpec
import com.jessebrault.ssg.task.Task
import com.jessebrault.ssg.util.Result

interface BuildTasksConverter {
    Result<Collection<Task>> convert(BuildSpec buildScriptResult)
}
