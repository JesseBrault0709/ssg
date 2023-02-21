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
            task.output.file.write(task.output.getContent(
                    context.allTasks, context.allTypes, context.onDiagnostics
            ))
        }

        @Override
        String toString() {
            'SpecialPageToHtmlFileTaskExecutor()'
        }

    }

    private static final TaskType<SpecialPageToHtmlFileTask> type = new TaskType<>(
            'specialPageToHtmlFile', new SpecialPageToHtmlFileTaskExecutor()
    )

    final SpecialPage input
    final HtmlFileOutput output

    SpecialPageToHtmlFileTask(String name, SpecialPage input, HtmlFileOutput output) {
        super(type, name)
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
