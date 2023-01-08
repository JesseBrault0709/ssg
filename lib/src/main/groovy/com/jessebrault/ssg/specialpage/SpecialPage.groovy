package com.jessebrault.ssg.specialpage

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@TupleConstructor(defaults = false)
@NullCheck
@EqualsAndHashCode
class SpecialPage {

    String text
    String path
    SpecialPageType type

    @Override
    String toString() {
        "SpecialPage(path: ${ this.path }, type: ${ this.type })"
    }

}
