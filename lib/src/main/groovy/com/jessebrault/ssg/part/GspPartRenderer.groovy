package com.jessebrault.ssg.part

import com.jessebrault.ssg.Diagnostic
import com.jessebrault.ssg.tagbuilder.DynamicTagBuilder
import com.jessebrault.ssg.text.EmbeddableText
import com.jessebrault.ssg.url.PathBasedUrlBuilder
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
            @Nullable EmbeddableText text = null,
            Collection<Part> allParts,
            String path
    ) {
        Objects.requireNonNull(part)
        Objects.requireNonNull(binding)
        Objects.requireNonNull(globals)
        Objects.requireNonNull(allParts)
        Objects.requireNonNull(path)
        def embeddedPartDiagnostics = []
        try {
            def result = engine.createTemplate(part.text).make([
                    binding: binding,
                    globals: globals,
                    parts: new EmbeddablePartsMap(allParts, globals, embeddedPartDiagnostics.&addAll, text, path),
                    path: path,
                    tagBuilder: new DynamicTagBuilder(),
                    text: text,
                    urlBuilder: new PathBasedUrlBuilder(path)
            ])
            new Tuple2<>([], result.toString())
        } catch (Exception e) {
            def diagnostic = new Diagnostic(
                    "An exception occurred while rendering part ${ part.path }:\n${ e }",
                    e
            )
            new Tuple2<>(
                    [diagnostic, *embeddedPartDiagnostics],
                    ''
            )
        }
    }

    @Override
    String toString() {
        "GspPartRenderer()"
    }

}
