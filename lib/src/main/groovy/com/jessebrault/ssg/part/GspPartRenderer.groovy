package com.jessebrault.ssg.part

import com.jessebrault.ssg.Diagnostic
import com.jessebrault.ssg.tagbuilder.DynamicTagBuilder
import com.jessebrault.ssg.text.EmbeddableText
import groovy.text.GStringTemplateEngine
import groovy.text.TemplateEngine
import groovy.transform.EqualsAndHashCode
import org.jetbrains.annotations.Nullable

@EqualsAndHashCode
class GspPartRenderer implements PartRenderer {

    private static final TemplateEngine engine = new GStringTemplateEngine()

    @Override
    Tuple2<Collection<Diagnostic>, String> render(
            Part part,
            Map binding,
            Map globals,
            @Nullable EmbeddableText text = null
    ) {
        Objects.requireNonNull(part)
        Objects.requireNonNull(binding)
        Objects.requireNonNull(globals)
        try {
            def result = engine.createTemplate(part.text).make([
                    binding: binding,
                    globals: globals,
                    tagBuilder: new DynamicTagBuilder(),
                    text: text
            ])
            new Tuple2<>([], result.toString())
        } catch (Exception e) {
            new Tuple2<>(
                    [new Diagnostic("An exception occurred while rendering part ${ part.path }:\n${ e }", e)],
                    ''
            )
        }
    }

    @Override
    String toString() {
        "GspPartRenderer()"
    }

}
