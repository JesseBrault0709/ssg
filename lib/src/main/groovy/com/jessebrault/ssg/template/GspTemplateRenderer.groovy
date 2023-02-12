package com.jessebrault.ssg.template

import com.jessebrault.ssg.Diagnostic
import com.jessebrault.ssg.part.EmbeddablePartsMap
import com.jessebrault.ssg.part.Part
import com.jessebrault.ssg.tagbuilder.DynamicTagBuilder
import com.jessebrault.ssg.text.EmbeddableText
import com.jessebrault.ssg.text.FrontMatter
import com.jessebrault.ssg.text.Text
import groovy.text.GStringTemplateEngine
import groovy.text.TemplateEngine
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck

@NullCheck
@EqualsAndHashCode
class GspTemplateRenderer implements TemplateRenderer {

    private static final TemplateEngine engine = new GStringTemplateEngine()

    @Override
    Tuple2<Collection<Diagnostic>, String> render(
            Template template,
            FrontMatter frontMatter,
            Text text,
            Collection<Part> parts,
            Map globals
    ) {
        try {
            Collection<Diagnostic> diagnostics = []
            def onDiagnostics = { Collection<Diagnostic> partDiagnostics ->
                diagnostics.addAll(partDiagnostics)
            }
            def embeddableText = new EmbeddableText(text, globals, onDiagnostics)
            def result = engine.createTemplate(template.text).make([
                    frontMatter: frontMatter,
                    globals: globals,
                    parts: new EmbeddablePartsMap(parts, globals, onDiagnostics, embeddableText),
                    tagBuilder: new DynamicTagBuilder(),
                    text: embeddableText
            ])
            new Tuple2<>(diagnostics, result.toString())
        } catch (Exception e) {
            new Tuple2<>([new Diagnostic("An exception occurred while rendering Template ${ template.path }:\n${ e }", e)], '')
        }
    }

    @Override
    String toString() {
        "GspTemplateRenderer()"
    }

}
