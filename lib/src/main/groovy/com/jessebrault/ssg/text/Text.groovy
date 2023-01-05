package com.jessebrault.ssg.text

import groovy.transform.Canonical
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@Canonical
@TupleConstructor(defaults = false)
@NullCheck
class Text {
    String text
    String path
    TextType type
}
