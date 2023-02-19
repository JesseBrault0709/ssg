package com.jessebrault.ssg.task

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@TupleConstructor(defaults = false)
@NullCheck
@EqualsAndHashCode
final class OutputMeta {

    final String sourcePath
    final String targetPath

    @Override
    String toString() {
        "OutputMeta(sourcePath: ${ sourcePath }, targetPath: ${ targetPath })"
    }

}
