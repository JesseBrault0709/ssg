package com.jessebrault.ssg.template

import com.jessebrault.ssg.part.Part
import com.jessebrault.ssg.part.PartsMap
import groovy.text.GStringTemplateEngine
import groovy.text.TemplateEngine

class GspTemplateRenderer implements TemplateRenderer {

    private static final TemplateEngine engine = new GStringTemplateEngine()

    @Override
    String render(Template template, String text, Collection<Part> parts) {
        engine.createTemplate(template.text).make([
                text: text,
                parts: new PartsMap(parts)
        ])
    }

}
