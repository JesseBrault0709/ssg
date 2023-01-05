package com.jessebrault.ssg.util

import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@TupleConstructor(includeFields = true, defaults = false)
@NullCheck
class RelativePathHandler {

    private final String relativePath

    String getWithoutExtension() {
        this.relativePath.subSequence(0, this.relativePath.lastIndexOf('.'))
    }

}
