package com.jessebrault.ssg.html

import com.jessebrault.ssg.model.Model
import com.jessebrault.ssg.template.Template
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@TupleConstructor(defaults = false)
@NullCheck(includeGenerated = true)
@EqualsAndHashCode
final class ModelToHtmlSpec<T> {
    final Model<T> model
    final Template template
    final String path
}
