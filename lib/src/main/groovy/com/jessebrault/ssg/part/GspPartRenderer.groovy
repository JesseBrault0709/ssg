package com.jessebrault.ssg.part

import groovy.text.GStringTemplateEngine
import groovy.text.TemplateEngine
import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class GspPartRenderer implements PartRenderer {

    private static final TemplateEngine engine = new GStringTemplateEngine()

    @Override
    String render(String partText, Map binding, Map globals) {
        engine.createTemplate(partText).make([
                binding: binding,
                globals: globals
        ])
    }

    @Override
    String toString() {
        "GspPartRenderer()"
    }

}
