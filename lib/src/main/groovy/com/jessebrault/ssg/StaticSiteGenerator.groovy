package com.jessebrault.ssg

import com.jessebrault.ssg.task.TaskContainer
import com.jessebrault.ssg.task.TaskTypeContainer

interface StaticSiteGenerator {
    TaskTypeContainer getTaskTypes()
    Result<TaskContainer> generate(Build build)
}
