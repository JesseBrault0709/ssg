package com.jessebrault.ssg.html

import com.jessebrault.ssg.provider.CollectionProvider
import com.jessebrault.ssg.provider.CollectionProviders
import com.jessebrault.ssg.task.AbstractRenderTaskFactory
import com.jessebrault.ssg.task.Task
import com.jessebrault.ssg.task.TaskSpec
import com.jessebrault.ssg.util.Diagnostic
import com.jessebrault.ssg.util.Result

import static java.util.Objects.requireNonNull

final class TextToHtmlTaskFactory extends AbstractRenderTaskFactory {

    CollectionProvider<Result<TextToHtmlSpec>> specsProvider = CollectionProviders.getEmpty()

    @Override
    Result<Collection<Task>> getTasks(TaskSpec taskSpec) {
        super.checkProviders()
        requireNonNull(this.specsProvider)

        def allTexts = this.allTextsProvider.provide()
        def allModels = this.allModelsProvider.provide()
        def allParts = this.allPartsProvider.provide()

        Collection<Diagnostic> diagnostics = []

        final Collection<Task> tasks = this.specsProvider.provide().findResults {
            if (it.hasDiagnostics()) {
                diagnostics.addAll(it.diagnostics)
            } else {
                def spec = it.get()
                new TextToHtmlTask(
                        spec.toRelativeHtmlPath.apply(taskSpec),
                        taskSpec,
                        spec.text,
                        spec.template,
                        allTexts,
                        allModels,
                        allParts
                )
            }
        }
        Result.of(diagnostics, tasks)
    }

}
