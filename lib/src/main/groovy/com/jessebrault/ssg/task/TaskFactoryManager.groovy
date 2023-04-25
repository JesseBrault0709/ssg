package com.jessebrault.ssg.task

import com.jessebrault.ssg.provider.Provider
import groovy.io.FileType

final class TaskFactoryManager {

    Collection<TaskFactory> getAllFactories(Collection<Provider<File>> factoryPluginDirectoryProviders) {
        def factories = []

        def gcl = new GroovyClassLoader()
        def pluginDirectories = factoryPluginDirectoryProviders.collect { it.provide() }
        pluginDirectories.each {
            it.eachFileRecurse(FileType.FILES) {
                def cl = gcl.parseClass(it)
                def constructor = cl.getDeclaredConstructor()
                def factory = constructor.newInstance() as TaskFactory
                factories << factory
            }
        }



        factories
    }

}
