package com.jessebrault.ssg.html

import com.jessebrault.ssg.task.AbstractTask
import com.jessebrault.ssg.task.Task
import com.jessebrault.ssg.util.Diagnostic
import com.jessebrault.ssg.util.Result
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck

@NullCheck
@EqualsAndHashCode
abstract class AbstractHtmlTask extends AbstractTask implements HtmlTask {

    final String path
    private final File buildDir

    AbstractHtmlTask(String name, String path, File buildDir) {
        super(name)
        this.path = path
        this.buildDir = buildDir
    }

    protected abstract Result<String> transform(Collection<Task> allTasks)

    @Override
    final Collection<Diagnostic> execute(Collection<Task> allTasks) {
        def transformResult = this.transform(allTasks)
        if (transformResult.hasDiagnostics()) {
            transformResult.diagnostics
        } else {
            def content = transformResult.get()
            def target = new File(this.buildDir, this.path)
            target.createParentDirectories()
            target.write(content)
            []
        }
    }

    @Override
    String toString() {
        "AbstractHtmlTask(path: ${ this.path }, super: ${ super.toString() })"
    }

}
