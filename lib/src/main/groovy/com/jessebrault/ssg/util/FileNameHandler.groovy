package com.jessebrault.ssg.util

import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@TupleConstructor(includeFields = true, defaults = false)
@NullCheck
class FileNameHandler {

    private final File file

    String getExtension() {
        this.file.name.substring(this.file.name.lastIndexOf('.'))
    }

    String getWithoutExtension() {
        this.file.name.substring(0, this.file.name.lastIndexOf('.'))
    }

}
