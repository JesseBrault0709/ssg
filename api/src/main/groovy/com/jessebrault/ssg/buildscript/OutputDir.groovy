package com.jessebrault.ssg.buildscript

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck

@NullCheck
@EqualsAndHashCode
final class OutputDir {

    final String path

    OutputDir(String path) {
        this.path = path
    }

    OutputDir(File file) {
        this(file.path)
    }

    File asFile() {
        new File(this.path)
    }

    String asString() {
        this.path
    }

    Object asType(Class<?> clazz) {
        switch (clazz) {
            case File -> this.asFile()
            case String -> this.asString()
            default -> throw new IllegalArgumentException('cannot cast to a class other than File or String')
        }
    }

    @Override
    String toString() {
        "OutputDir(path: ${ this.path })"
    }

}
