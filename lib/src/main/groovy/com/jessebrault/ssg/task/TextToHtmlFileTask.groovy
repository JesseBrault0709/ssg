package com.jessebrault.ssg.task

import com.jessebrault.ssg.text.Text
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck

@NullCheck
@EqualsAndHashCode(callSuper = true)
final class TextToHtmlFileTask extends AbstractTask<TextToHtmlFileTask> {

    private static final class TextToHtmlFileTaskExecutor implements TaskExecutor<TextToHtmlFileTask> {

        @Override
        void execute(TextToHtmlFileTask task, TaskExecutorContext context) {
            task.output.file.write(task.output.getContent(
                    context.allTasks, context.allTypes, context.onDiagnostics
            ))
        }

        @Override
        String toString() {
            'TextToHtmlFileTaskExecutor()'
        }

    }

    static final TaskType<TextToHtmlFileTask> TYPE = new TaskType<>(
            'textToHtmlFile', new TextToHtmlFileTaskExecutor()
    )

    final Text input
    final HtmlFileOutput output

    TextToHtmlFileTask(String name, Text input, HtmlFileOutput output) {
        super(TYPE, name)
        this.input = input
        this.output = output
    }

    @Override
    protected TextToHtmlFileTask getThis() {
        this
    }

    @Override
    String toString() {
        "TextToHtmlFileTask(input: ${ this.input }, output: ${ this.output }, super: ${ super })"
    }

}
