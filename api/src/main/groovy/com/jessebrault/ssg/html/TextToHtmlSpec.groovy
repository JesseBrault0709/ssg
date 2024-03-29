package com.jessebrault.ssg.html

import com.jessebrault.ssg.task.TaskSpec
import com.jessebrault.ssg.template.Template
import com.jessebrault.ssg.text.Text
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

import java.util.function.Function

@TupleConstructor(defaults = false)
@NullCheck(includeGenerated = true)
@EqualsAndHashCode
final class TextToHtmlSpec {
    final Text text
    final Template template
    final Function<TaskSpec, String> toRelativeHtmlPath
}
