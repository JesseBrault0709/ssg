package com.jessebrault.ssg.html

import com.jessebrault.ssg.template.Template
import com.jessebrault.ssg.text.Text
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@TupleConstructor(defaults = false)
@NullCheck(includeGenerated = true)
@EqualsAndHashCode
final class TextToHtmlSpec {
    final Text text
    final Template template
    final String path
}
