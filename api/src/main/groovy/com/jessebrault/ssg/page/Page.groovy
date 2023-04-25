package com.jessebrault.ssg.page

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@TupleConstructor(defaults = false)
@NullCheck(includeGenerated = true)
@EqualsAndHashCode
final class Page {

    final String path
    final PageType type
    final String text

    @Override
    String toString() {
        "Page(path: ${ this.path }, type: ${ this.type })"
    }

}
