package com.jessebrault.ssg.task

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@TupleConstructor(defaults = false)
@NullCheck
@EqualsAndHashCode
final class Output {

    final OutputMeta meta
    final String content

    @Override
    String toString() {
        "Output(meta: ${ this.meta })"
    }

}
