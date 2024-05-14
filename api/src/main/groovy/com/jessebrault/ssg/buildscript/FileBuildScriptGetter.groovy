package com.jessebrault.ssg.buildscript

import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@NullCheck
@TupleConstructor(includeFields = true)
final class FileBuildScriptGetter {

    private final GroovyClassLoader groovyClassLoader

    BuildScriptBase getBuildScript(String name) {
        Class<?> scriptClass = this.groovyClassLoader.loadClass(name, true, false)
        def scriptObject = scriptClass.getConstructor().newInstance()
        assert scriptObject instanceof BuildScriptBase
        scriptObject
    }

}
