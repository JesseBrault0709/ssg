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

@TupleConstructor(defaults = false)
@NullCheck(includeGenerated = true)
@EqualsAndHashCode
final class SourceProviders {

    static final Monoid<SourceProviders> DEFAULT_MONOID = Monoids.of(getEmpty(), SourceProviders::concat)

    static SourceProviders concat(SourceProviders sp0, SourceProviders sp1) {
        new SourceProviders(
                sp0.textsProvider + sp1.textsProvider,
                sp0.modelsProvider + sp1.modelsProvider,
                sp0.pagesProvider + sp1.pagesProvider,
                sp0.templatesProvider + sp1.templatesProvider,
                sp0.partsProvider + sp1.partsProvider
        )
    }

    static SourceProviders get(Map<String, Object> args) {
        new SourceProviders(
                args?.textsProvider as CollectionProvider<Text>
                        ?: CollectionProviders.getEmpty() as CollectionProvider<Text>,
                args?.modelsProvider as CollectionProvider<Model<Object>>
                        ?: CollectionProviders.getEmpty() as CollectionProvider<Model<Object>>,
                args?.pagesProvider as CollectionProvider<Page>
                        ?: CollectionProviders.getEmpty() as CollectionProvider<Page>,
                args?.templatesProvider as CollectionProvider<Template>
                        ?: CollectionProviders.getEmpty() as CollectionProvider<Template>,
                args?.partsProvider as CollectionProvider<Part>
                        ?: CollectionProviders.getEmpty() as CollectionProvider<Part>
        )
    }

    static SourceProviders getEmpty() {
        new SourceProviders(
                CollectionProviders.getEmpty(),
                CollectionProviders.getEmpty(),
                CollectionProviders.getEmpty(),
                CollectionProviders.getEmpty(),
                CollectionProviders.getEmpty()
        )
    }

    final CollectionProvider<Text> textsProvider
    final CollectionProvider<Model<Object>> modelsProvider
    final CollectionProvider<Page> pagesProvider
    final CollectionProvider<Template> templatesProvider
    final CollectionProvider<Part> partsProvider

    SourceProviders plus(SourceProviders other) {
        concat(this, other)
    }

    @Override
    String toString() {
        "SourceProviders(textsProvider: ${ this.textsProvider }, modelsProvider: ${ this.modelsProvider }, " +
                "pagesProvider: ${ this.pagesProvider }, templatesProvider: ${ this.templatesProvider }, " +
                "partsProvider: ${ this.partsProvider })"
    }

}
