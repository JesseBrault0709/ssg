package com.jessebrault.ssg.specialpage

import com.jessebrault.ssg.task.Input
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@TupleConstructor(defaults = false)
@NullCheck
@EqualsAndHashCode
class SpecialPage implements Input {

    String text
    String path
    SpecialPageType type

    @Override
    String toString() {
        "SpecialPage(path: ${ this.path }, type: ${ this.type })"
    }

}
