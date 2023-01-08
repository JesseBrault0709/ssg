package com.jessebrault.ssg.util

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@TupleConstructor(includeFields = true, defaults = false)
@NullCheck
@EqualsAndHashCode(includeFields = true)
class RelativePathHandler {

    private final String relativePath

    String getWithoutExtension() {
        this.relativePath.subSequence(0, this.relativePath.lastIndexOf('.'))
    }

    @Override
    String toString() {
        "RelativePathHandler(relativePath: ${ this.relativePath })"
    }

}
