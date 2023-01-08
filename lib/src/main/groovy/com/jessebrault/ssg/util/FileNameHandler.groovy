package com.jessebrault.ssg.util

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@TupleConstructor(includeFields = true, defaults = false)
@NullCheck
@EqualsAndHashCode(includeFields = true)
class FileNameHandler {

    private final File file

    String getExtension() {
        def lastIndexOfDot = this.file.name.lastIndexOf('.')
        if (lastIndexOfDot == -1) {
            ''
        } else {
            this.file.name.substring(lastIndexOfDot)
        }
    }

    String getWithoutExtension() {
        this.file.name.substring(0, this.file.name.lastIndexOf('.'))
    }

    @Override
    String toString() {
        "FileNameHandler(file: ${ this.file })"
    }

}
