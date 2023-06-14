package com.jessebrault.ssg.html


import com.jessebrault.ssg.provider.CollectionProvider
import com.jessebrault.ssg.provider.CollectionProviders
import com.jessebrault.ssg.task.AbstractRenderTaskFactory
import com.jessebrault.ssg.task.Task
import com.jessebrault.ssg.task.TaskSpec
import com.jessebrault.ssg.util.Result

import static java.util.Objects.requireNonNull

final class PageToHtmlTaskFactory extends AbstractRenderTaskFactory {

    CollectionProvider<PageToHtmlSpec> specsProvider = CollectionProviders.getEmpty()

    @Override
    Result<Collection<Task>> getTasks(TaskSpec taskSpec) {
        this.checkProviders()
        requireNonNull(this.specsProvider)

        def allTexts = this.allTextsProvider.provide()
        def allModels = this.allModelsProvider.provide()
        def allParts = this.allPartsProvider.provide()

        final Collection<Task> tasks = this.specsProvider.provide()
                .collect {
                    new PageToHtmlTask(
                            it.toRelativeHtmlPath.apply(taskSpec),
                            taskSpec,
                            it.page,
                            allTexts,
                            allModels,
                            allParts
                    )
                }
        Result.of(tasks)
    }
}
