package com.jessebrault.ssg.buildscript

import com.jessebrault.ssg.model.Model
import com.jessebrault.ssg.page.Page
import com.jessebrault.ssg.part.Part
import com.jessebrault.ssg.provider.CollectionProvider
import com.jessebrault.ssg.provider.CollectionProviders
import com.jessebrault.ssg.template.Template
import com.jessebrault.ssg.text.Text
import com.jessebrault.ssg.util.Monoid
import com.jessebrault.ssg.util.Monoids
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@NullCheck
@EqualsAndHashCode
final class SourceProviders {

    static final Monoid<SourceProviders> DEFAULT_MONOID = Monoids.of(getEmpty(), SourceProviders::concat)

    static SourceProviders concat(SourceProviders sp0, SourceProviders sp1) {
        new SourceProviders(
                sp0.textsProvider + sp1.textsProvider,
                sp0.modelsProvider + sp1.modelsProvider,
                sp0.pagesProvider + sp1.pagesProvider,
                sp0.templatesProvider + sp1.templatesProvider,
                sp0.partsProvider + sp1.partsProvider,
                sp0.custom + sp1.custom
        )
    }

    static SourceProviders get(Map<String, Object> args) {
        new SourceProviders(
                args.textsProvider as CollectionProvider<Text>
                        ?: CollectionProviders.getEmpty() as CollectionProvider<Text>,
                args.modelsProvider as CollectionProvider<Model<Object>>
                        ?: CollectionProviders.getEmpty() as CollectionProvider<Model<Object>>,
                args.pagesProvider as CollectionProvider<Page>
                        ?: CollectionProviders.getEmpty() as CollectionProvider<Page>,
                args.templatesProvider as CollectionProvider<Template>
                        ?: CollectionProviders.getEmpty() as CollectionProvider<Template>,
                args.partsProvider as CollectionProvider<Part>
                        ?: CollectionProviders.getEmpty() as CollectionProvider<Part>,
                args.custom as Map<String, CollectionProvider<?>> ?: [:]
        )
    }

    static SourceProviders getEmpty() {
        new SourceProviders(
                CollectionProviders.getEmpty(),
                CollectionProviders.getEmpty(),
                CollectionProviders.getEmpty(),
                CollectionProviders.getEmpty(),
                CollectionProviders.getEmpty(),
                [:]
        )
    }

    final CollectionProvider<Text> textsProvider
    final CollectionProvider<Model<Object>> modelsProvider
    final CollectionProvider<Page> pagesProvider
    final CollectionProvider<Template> templatesProvider
    final CollectionProvider<Part> partsProvider

    private final Map<String, CollectionProvider<Object>> custom

    SourceProviders(
            CollectionProvider<Text> textsProvider,
            CollectionProvider<Model<Object>> modelsProvider,
            CollectionProvider<Page> pagesProvider,
            CollectionProvider<Template> templatesProvider,
            CollectionProvider<Part> partsProvider,
            Map<String, CollectionProvider<Object>> custom
    ) {
        this.textsProvider = textsProvider
        this.modelsProvider = modelsProvider
        this.pagesProvider = pagesProvider
        this.templatesProvider = templatesProvider
        this.partsProvider = partsProvider
        this.custom = custom
    }

    SourceProviders plus(SourceProviders other) {
        concat(this, other)
    }

    def <T> CollectionProvider<T> getCustom(String name, Class<T> tClass) {
        this.custom.get(name) as CollectionProvider<T>
    }

    void putCustom(String name, CollectionProvider<?> customProvider) {
        this.custom.put(name, customProvider as CollectionProvider<Object>)
    }

    Map<String, CollectionProvider<Object>> getAllCustom() {
        this.custom
    }

    @Override
    String toString() {
        "SourceProviders(textsProvider: ${ this.textsProvider }, modelsProvider: ${ this.modelsProvider }, " +
                "pagesProvider: ${ this.pagesProvider }, templatesProvider: ${ this.templatesProvider }, " +
                "partsProvider: ${ this.partsProvider })"
    }

}
