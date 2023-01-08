package com.jessebrault.ssg.part

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@TupleConstructor(defaults = false)
@NullCheck
@EqualsAndHashCode
class Part {

    String path
    PartType type
    String text

    @Override
    String toString() {
        "Part(path: ${ this.path }, type: ${ this.type })"
    }

}
