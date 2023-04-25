package com.jessebrault.ssg.part

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@TupleConstructor(defaults = false)
@NullCheck(includeGenerated = true)
@EqualsAndHashCode
final class PartType {

    Collection<String> ids
    PartRenderer renderer

    @Override
    String toString() {
        "PartType(ids: ${ this.ids }, renderer: ${ this.renderer })"
    }

}
