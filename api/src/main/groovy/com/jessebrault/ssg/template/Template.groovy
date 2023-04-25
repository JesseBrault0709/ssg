package com.jessebrault.ssg.template

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@TupleConstructor(defaults = false)
@NullCheck(includeGenerated = true)
@EqualsAndHashCode
final class Template {

    String path
    TemplateType type
    String text

    @Override
    String toString() {
        "Template(path: ${ this.path }, type: ${ this.type })"
    }

}
