package com.jessebrault.ssg.buildscript.delegates

import com.jessebrault.ssg.buildscript.SourceProviders
import com.jessebrault.ssg.model.Model
import com.jessebrault.ssg.page.Page
import com.jessebrault.ssg.part.Part
import com.jessebrault.ssg.provider.CollectionProvider
import com.jessebrault.ssg.provider.CollectionProviders
import com.jessebrault.ssg.template.Template
import com.jessebrault.ssg.text.Text
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck

@NullCheck
@EqualsAndHashCode
final class SourceProvidersDelegate {

    private CollectionProvider<Text> textsProvider = CollectionProviders.getEmpty()
    private CollectionProvider<Model<Object>> modelsProvider = CollectionProviders.getEmpty()
    private CollectionProvider<Page> pagesProvider = CollectionProviders.getEmpty()
    private CollectionProvider<Template> templatesProvider = CollectionProviders.getEmpty()
    private CollectionProvider<Part> partsProvider = CollectionProviders.getEmpty()

    private final Map<String, CollectionProvider<Object>> custom = [:]

    void texts(CollectionProvider<Text> textsProvider) {
        this.textsProvider += textsProvider
    }

    void models(CollectionProvider<Model<?>> modelsProvider) {
        this.modelsProvider += modelsProvider
    }

    void pages(CollectionProvider<Page> pagesProvider) {
        this.pagesProvider += pagesProvider
    }

    void templates(CollectionProvider<Template> templatesProvider) {
        this.templatesProvider += templatesProvider
    }

    void parts(CollectionProvider<Part> partsProvider) {
        this.partsProvider += partsProvider
    }

    void custom(String name, CollectionProvider<?> customProvider) {
        this.custom.put(name, customProvider as CollectionProvider<Object>)
    }

    SourceProviders getResult() {
        new SourceProviders(
                this.textsProvider,
                this.modelsProvider,
                this.pagesProvider,
                this.templatesProvider,
                this.partsProvider,
                this.custom
        )
    }

}
