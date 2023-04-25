package com.jessebrault.ssg.html

import com.jessebrault.ssg.page.Page
import com.jessebrault.ssg.provider.CollectionProvider
import com.jessebrault.ssg.provider.CollectionProviders
import com.jessebrault.ssg.task.AbstractRenderTaskFactory
import com.jessebrault.ssg.task.Task
import com.jessebrault.ssg.task.TaskSpec
import com.jessebrault.ssg.util.Result

import static com.jessebrault.ssg.util.ExtensionUtil.stripExtension
import static java.util.Objects.requireNonNull

final class PageToHtmlTaskFactory extends AbstractRenderTaskFactory {

    CollectionProvider<Page> pagesProvider = CollectionProviders.getEmpty()

    @Override
    Result<Collection<Task>> getTasks(TaskSpec taskSpec) {
        super.checkProviders()
        requireNonNull(this.pagesProvider)

        def allTexts = this.allTextsProvider.provide()
        def allModels = this.allModelsProvider.provide()
        def allParts = this.allPartsProvider.provide()

        final Collection<Task> tasks = this.pagesProvider.provide()
                .collect {
                    new PageToHtmlTask(
                            stripExtension(it.path) + '.html',
                            taskSpec,
                            it,
                            allTexts,
                            allModels,
                            allParts
                    )
                }
        Result.of(tasks)
    }
}
