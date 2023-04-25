package com.jessebrault.ssg

import com.jessebrault.ssg.buildscript.Build
import com.jessebrault.ssg.task.Task
import com.jessebrault.ssg.task.TaskSpec
import com.jessebrault.ssg.util.Diagnostic
import com.jessebrault.ssg.util.Result

final class SimpleBuildTasksConverter implements BuildTasksConverter {

    @Override
    Result<Collection<Task>> convert(Build buildScriptResult) {
        def taskSpec = new TaskSpec(
                buildScriptResult.name,
                buildScriptResult.outputDirFunction.apply(buildScriptResult).file,
                buildScriptResult.siteSpec,
                buildScriptResult.globals
        )
        Collection<Task> tasks = []
        Collection<Diagnostic> diagnostics = []

        buildScriptResult.taskFactorySpecs.each {
            def factory = it.supplier.get()
            it.configureClosures.each { it(factory) }
            def result = factory.getTasks(taskSpec)
            diagnostics.addAll(result.diagnostics)
            tasks.addAll(result.get())
        }

        Result.of(diagnostics, tasks)
    }

}
