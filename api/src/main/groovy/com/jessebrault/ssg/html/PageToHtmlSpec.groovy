package com.jessebrault.ssg.html

import com.jessebrault.ssg.page.Page
import com.jessebrault.ssg.task.TaskSpec
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

import java.util.function.Function

@TupleConstructor(defaults = false)
@NullCheck(includeGenerated = true)
@EqualsAndHashCode
final class PageToHtmlSpec {
    final Page page
    final Function<TaskSpec, String> toRelativeHtmlPath
}
