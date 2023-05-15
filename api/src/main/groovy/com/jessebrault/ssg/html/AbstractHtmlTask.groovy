package com.jessebrault.ssg.html

import com.jessebrault.ssg.task.AbstractTask
import com.jessebrault.ssg.task.Task
import com.jessebrault.ssg.task.TaskInput
import com.jessebrault.ssg.util.Diagnostic
import com.jessebrault.ssg.util.Result
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import org.jsoup.Jsoup

@NullCheck
@EqualsAndHashCode
abstract class AbstractHtmlTask<I extends TaskInput> extends AbstractTask implements HtmlTask {

    final String htmlPath
    final I input
    final HtmlOutput output

    AbstractHtmlTask(
            String name,
            String htmlPath,
            I input,
            File buildDir
    ) {
        super(name)
        this.htmlPath = htmlPath
        this.input = input
        this.output = new SimpleHtmlOutput(
                "htmlOutput:${ htmlPath }",
                new File(buildDir, htmlPath),
                htmlPath
        )
    }

    protected abstract Result<String> transform(Collection<Task> allTasks)

    @Override
    final Collection<Diagnostic> execute(Collection<Task> allTasks) {
        def transformResult = this.transform(allTasks)
        if (transformResult.hasDiagnostics()) {
            transformResult.diagnostics
        } else {
            def content = transformResult.get()
            def document = Jsoup.parse(content)
            document.outputSettings().indentAmount(4)
            def formatted = document.toString()
            this.output.file.createParentDirectories()
            this.output.file.write(formatted)
            []
        }
    }

    @Override
    String toString() {
        "AbstractHtmlTask(path: ${ this.htmlPath }, super: ${ super.toString() })"
    }

}
