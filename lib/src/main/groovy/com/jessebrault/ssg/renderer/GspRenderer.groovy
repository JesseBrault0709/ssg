package com.jessebrault.ssg.renderer

import com.jessebrault.ssg.template.Template
import groovy.text.GStringTemplateEngine
import groovy.text.TemplateEngine

class GspRenderer implements Renderer {

    private static final TemplateEngine engine = new GStringTemplateEngine()

    @Override
    String render(Template template, String text) {
        engine.createTemplate(template.file.text).make([
                text: text
        ])
    }

}
