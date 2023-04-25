package com.jessebrault.ssg.task.collector

import com.jessebrault.ssg.provider.Provider
import com.jessebrault.ssg.task.TaskFactory
import groovy.io.FileType
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@TupleConstructor(defaults = false, includeFields = true)
@NullCheck(includeGenerated = true)
final class GroovyFileTaskFactoryCollector implements TaskFactoryCollector {

    private final GroovyClassLoader groovyClassLoader
    private final Collection<Provider<File>> factoryDirectoryProviders

    @Override
    Collection<TaskFactory> getAllFactories() {
        Collection<TaskFactory> factories = []

        def pluginDirectories = this.factoryDirectoryProviders.collect { it.provide() }
        pluginDirectories.each {
            it.eachFileRecurse(FileType.FILES) {
                def cl = this.groovyClassLoader.parseClass(it)
                if (TaskFactory.isAssignableFrom(cl)) {
                    def constructor = cl.getDeclaredConstructor()
                    def factory = constructor.newInstance() as TaskFactory
                    factories << factory
                }
            }
        }

        factories
    }

}
