package com.jessebrault.ssg.template

import groovy.transform.Canonical
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@Canonical
@TupleConstructor(defaults = false)
@NullCheck
class Template {
    String text
    String relativePath
    TemplateType type
}
