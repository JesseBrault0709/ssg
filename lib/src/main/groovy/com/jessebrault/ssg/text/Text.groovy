package com.jessebrault.ssg.text

import com.jessebrault.ssg.input.InputPage
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@TupleConstructor(defaults = false)
@NullCheck
@EqualsAndHashCode
class Text implements InputPage {

    String text
    String path
    TextType type

    @Override
    String toString() {
        "Text(path: ${ this.path }, type: ${ this.type })"
    }

}
