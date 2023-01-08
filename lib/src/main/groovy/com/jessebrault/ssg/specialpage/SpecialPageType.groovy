package com.jessebrault.ssg.specialpage

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@TupleConstructor(defaults = false)
@NullCheck
@EqualsAndHashCode
class SpecialPageType {

    Collection<String> ids
    SpecialPageRenderer renderer

    @Override
    String toString() {
        "SpecialPageType(ids: ${ this.ids }, renderer: ${ this.renderer })"
    }

}
