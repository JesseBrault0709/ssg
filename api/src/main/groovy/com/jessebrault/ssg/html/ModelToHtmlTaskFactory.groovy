package com.jessebrault.ssg.html

import com.jessebrault.ssg.provider.CollectionProvider
import com.jessebrault.ssg.provider.CollectionProviders
import com.jessebrault.ssg.task.AbstractRenderTaskFactory
import com.jessebrault.ssg.task.Task
import com.jessebrault.ssg.task.TaskSpec
import com.jessebrault.ssg.util.Result

final class ModelToHtmlTaskFactory<T> extends AbstractRenderTaskFactory {

    CollectionProvider<ModelToHtmlSpec<T>> specsProvider = CollectionProviders.getEmpty()

    @Override
    Result<Collection<Task>> getTasks(TaskSpec taskSpec) {
        def allTexts = this.allTextsProvider.provide()
        def allModels = this.allModelsProvider.provide()
        def allParts = this.allPartsProvider.provide()

        Result.of(specsProvider.provide().collect {
            new ModelToHtmlTask<>(
                    it.path,
                    taskSpec,
                    it.model,
                    it.template,
                    allTexts,
                    allModels,
                    allParts
            )
        })
    }

}
