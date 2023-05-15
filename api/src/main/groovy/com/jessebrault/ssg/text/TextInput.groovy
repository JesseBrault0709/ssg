package com.jessebrault.ssg.text

import com.jessebrault.ssg.task.TaskInput
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@TupleConstructor(defaults = false)
@NullCheck(includeGenerated = true)
@EqualsAndHashCode
final class TextInput implements TaskInput {
    final String name
    final Text text
}
