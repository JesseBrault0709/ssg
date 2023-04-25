package com.jessebrault.ssg.task

import com.jessebrault.ssg.model.Model
import com.jessebrault.ssg.part.Part
import com.jessebrault.ssg.provider.CollectionProvider
import com.jessebrault.ssg.provider.CollectionProviders
import com.jessebrault.ssg.text.Text

import static java.util.Objects.requireNonNull

abstract class AbstractRenderTaskFactory implements TaskFactory {

    CollectionProvider<Text> allTextsProvider = CollectionProviders.getEmpty()
    CollectionProvider<Model<Object>> allModelsProvider = CollectionProviders.getEmpty()
    CollectionProvider<Part> allPartsProvider = CollectionProviders.getEmpty()

    protected final void checkProviders() {
        requireNonNull(this.allTextsProvider)
        requireNonNull(this.allModelsProvider)
        requireNonNull(this.allPartsProvider)
    }

}
