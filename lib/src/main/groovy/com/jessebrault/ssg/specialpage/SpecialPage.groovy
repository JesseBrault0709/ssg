package com.jessebrault.ssg.specialpage

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@TupleConstructor(defaults = false)
@NullCheck(includeGenerated = true)
@EqualsAndHashCode
final class SpecialPage {

    final String text
    final String path
    final SpecialPageType type

    @Override
    String toString() {
        "SpecialPage(path: ${ this.path }, type: ${ this.type })"
    }

}
