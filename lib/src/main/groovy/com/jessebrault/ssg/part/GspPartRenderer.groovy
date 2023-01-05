package com.jessebrault.ssg.part

import groovy.text.GStringTemplateEngine
import groovy.text.TemplateEngine

class GspPartRenderer implements PartRenderer {

    private static final TemplateEngine engine = new GStringTemplateEngine()

    @Override
    String render(String partText, Map binding) {
        engine.createTemplate(partText).make(binding)
    }

}
