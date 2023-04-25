package com.jessebrault.ssg.task.collector

import com.jessebrault.ssg.task.TaskFactory
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@TupleConstructor(includeFields = true, defaults = false)
@NullCheck(includeGenerated = true)
final class ServiceTaskFactoryCollector implements TaskFactoryCollector {

    private final ClassLoader classLoader

    @Override
    Collection<TaskFactory> getAllFactories() {
        ServiceLoader.load(TaskFactory, this.classLoader).asList()
    }

}
