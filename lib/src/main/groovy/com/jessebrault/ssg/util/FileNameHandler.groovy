package com.jessebrault.ssg.util

import groovy.transform.TupleConstructor

@TupleConstructor(includeFields = true)
class FileNameHandler {

    private final File file

    String getExtension() {
        this.file.name.substring(this.file.name.lastIndexOf('.') + 1)
    }

}
