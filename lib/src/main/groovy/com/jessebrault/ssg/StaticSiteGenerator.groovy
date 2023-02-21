package com.jessebrault.ssg

import com.jessebrault.ssg.task.TaskContainer

interface StaticSiteGenerator {
    Result<TaskContainer> generate(Build build)
}
