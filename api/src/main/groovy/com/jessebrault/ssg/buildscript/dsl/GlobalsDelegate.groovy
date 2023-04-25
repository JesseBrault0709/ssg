package com.jessebrault.ssg.buildscript.dsl

final class GlobalsDelegate {

    @Delegate
    final Map<String, Object> globals = [:]

    @Override
    Object getProperty(String propertyName) {
        this.globals[propertyName]
    }

    @Override
    void setProperty(String propertyName, Object newValue) {
        this.globals[propertyName] = newValue
    }

    Map<String, Object> getResult() {
        this.globals
    }

}
