package com.jessebrault.ssg.task.collector

import com.jessebrault.ssg.task.TaskFactory

interface TaskFactoryCollector {
    Collection<TaskFactory> getAllFactories()
}
