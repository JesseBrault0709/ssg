package com.jessebrault.ssg.model

import com.jessebrault.ssg.task.TaskInput
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@TupleConstructor(defaults = false)
@NullCheck(includeGenerated = true)
@EqualsAndHashCode
final class ModelInput<T> implements TaskInput {
    final String name
    final Model<T> model
}
