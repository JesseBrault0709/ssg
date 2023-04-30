package com.jessebrault.ssg

import com.jessebrault.ssg.buildscript.Build
import com.jessebrault.ssg.task.Task
import com.jessebrault.ssg.task.TaskSpec
import com.jessebrault.ssg.util.Diagnostic
import com.jessebrault.ssg.util.Result

final class SimpleBuildTasksConverter implements BuildTasksConverter {

    @Override
    Result<Collection<Task>> convert(Build build) {
        def taskSpec = new TaskSpec(
                build.name,
                build.outputDirFunction.apply(build).asFile(),
                build.siteSpec,
                build.globals
        )
        Collection<Task> tasks = []
        Collection<Diagnostic> diagnostics = []

        build.taskFactorySpecs.each {
            def factory = it.supplier.get()
            it.configurators.each { it.accept(factory) }
            def result = factory.getTasks(taskSpec)
            diagnostics.addAll(result.diagnostics)
            tasks.addAll(result.get())
        }

        Result.of(diagnostics, tasks)
    }

}
