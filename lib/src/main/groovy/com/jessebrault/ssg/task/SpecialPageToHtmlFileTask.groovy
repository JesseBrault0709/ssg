package com.jessebrault.ssg.task

import com.jessebrault.ssg.specialpage.SpecialPage
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck

@NullCheck
@EqualsAndHashCode
final class SpecialPageToHtmlFileTask extends AbstractTask<SpecialPageToHtmlFileTask> {

    private static final class SpecialPageToHtmlFileTaskExecutor implements TaskExecutor<SpecialPageToHtmlFileTask> {

        @Override
        void execute(SpecialPageToHtmlFileTask task, TaskExecutorContext context) {
            task.output.file.createParentDirectories()
            task.output.file.write(task.output.getContent(
                    context.allTasks, context.allTypes, context.onDiagnostics
            ))
        }

        @Override
        String toString() {
            'SpecialPageToHtmlFileTaskExecutor()'
        }

    }

    static final TaskType<SpecialPageToHtmlFileTask> TYPE = new TaskType<>(
            'specialPageToHtmlFile', new SpecialPageToHtmlFileTaskExecutor()
    )

    final SpecialPage input
    final HtmlFileOutput output

    SpecialPageToHtmlFileTask(String name, SpecialPage input, HtmlFileOutput output) {
        super(TYPE, name)
        this.input = input
        this.output = output
    }

    @Override
    protected SpecialPageToHtmlFileTask getThis() {
        this
    }

    @Override
    String toString() {
        "SpecialPageToHtmlFileTask(input: ${ this.input }, output: ${ this.output }, super: ${ super })"
    }

}
