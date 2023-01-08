package com.jessebrault.ssg.part

import com.jessebrault.ssg.Diagnostic
import groovy.text.GStringTemplateEngine
import groovy.text.TemplateEngine
import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class GspPartRenderer implements PartRenderer {

    private static final TemplateEngine engine = new GStringTemplateEngine()

    @Override
    Tuple2<Collection<Diagnostic>, String> render(Part part, Map binding, Map globals) {
        try {
            def result = engine.createTemplate(part.text).make([
                    binding: binding,
                    globals: globals
            ])
            new Tuple2<>([], result.toString())
        } catch (Exception e) {
            new Tuple2<>([new Diagnostic("An exception occurred while rendering part ${ part.path }:\n${ e }", e)], '')
        }
    }

    @Override
    String toString() {
        "GspPartRenderer()"
    }

}
