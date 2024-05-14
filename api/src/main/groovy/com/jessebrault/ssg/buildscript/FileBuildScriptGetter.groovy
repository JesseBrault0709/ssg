package com.jessebrault.ssg.buildscript

import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@NullCheck
@TupleConstructor(includeFields = true)
final class FileBuildScriptGetter {

    private final File projectRoot
    private final GroovyClassLoader gcl

    BuildScriptBase getBuildInfo(String name) {
        Class<?> scriptClass = this.gcl.loadClass(name, true, false)
        def scriptObject = scriptClass.getConstructor().newInstance()
        assert scriptObject instanceof BuildScriptBase
        scriptObject
    }

}
