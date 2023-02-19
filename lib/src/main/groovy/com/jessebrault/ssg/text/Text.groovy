package com.jessebrault.ssg.text

import com.jessebrault.ssg.task.Input
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@TupleConstructor(defaults = false)
@NullCheck
@EqualsAndHashCode
class Text implements Input {

    String text
    String path
    TextType type

    @Override
    String toString() {
        "Text(path: ${ this.path }, type: ${ this.type })"
    }

}
