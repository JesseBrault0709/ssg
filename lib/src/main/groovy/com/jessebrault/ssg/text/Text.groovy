package com.jessebrault.ssg.text

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@TupleConstructor(defaults = false)
@NullCheck(includeGenerated = true)
@EqualsAndHashCode
final class Text {

    final String text
    final String path
    final TextType type

    @Override
    String toString() {
        "Text(path: ${ this.path }, type: ${ this.type })"
    }

}
