package com.jessebrault.ssg.task

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@TupleConstructor(defaults = false, includeFields = true)
@NullCheck(includeGenerated = true)
@EqualsAndHashCode
final class HtmlFileOutput {

    final File file
    final String htmlPath

    private final Closure<String> contentClosure

    String getContent(TaskContainer tasks, TaskTypeContainer taskTypes, Closure onDiagnostics) {
        this.contentClosure(tasks, taskTypes, onDiagnostics)
    }

    @Override
    String toString() {
        "HtmlFileOutput(file: ${ this.file }, htmlPath: ${ this.htmlPath })"
    }

}
