package com.jessebrault.ssg.page

import com.jessebrault.ssg.task.TaskInput
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@TupleConstructor(defaults = false)
@NullCheck(includeGenerated = true)
@EqualsAndHashCode
final class PageInput implements TaskInput {
    final String name
    final Page page
}
