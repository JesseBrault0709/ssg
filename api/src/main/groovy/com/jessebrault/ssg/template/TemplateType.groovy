package com.jessebrault.ssg.template

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@TupleConstructor(defaults = false)
@NullCheck(includeGenerated = true)
@EqualsAndHashCode
final class TemplateType {

    Collection<String> ids
    TemplateRenderer renderer

    @Override
    String toString() {
        "TemplateType(ids: ${ this.ids }, renderer: ${ this.renderer })"
    }

}
