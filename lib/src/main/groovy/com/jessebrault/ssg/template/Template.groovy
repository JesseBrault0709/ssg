package com.jessebrault.ssg.template

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@TupleConstructor(defaults = false)
@NullCheck
@EqualsAndHashCode
class Template {

    String text
    String relativePath
    TemplateType type

    @Override
    String toString() {
        "Template(path: ${ this.relativePath }, type: ${ this.type })"
    }

}
