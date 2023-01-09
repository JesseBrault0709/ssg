package com.jessebrault.ssg.buildscript

import groovy.transform.TupleConstructor

@TupleConstructor(includeFields = true, defaults = false)
 class GlobalsClosureDelegate {

    private final Map globals

    @Override
    Object getProperty(String propertyName) {
        this.globals[propertyName]
    }

    @Override
    void setProperty(String propertyName, Object newValue) {
        this.globals.put(propertyName, newValue)
    }

}
