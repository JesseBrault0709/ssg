package com.jessebrault.ssg

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@TupleConstructor(includeFields = true, defaults = false)
@NullCheck
@EqualsAndHashCode
class GeneratedPage {

    String path
    String html

    @Override
    String toString() {
        "GeneratedPage(path: ${ this.path })"
    }

}
