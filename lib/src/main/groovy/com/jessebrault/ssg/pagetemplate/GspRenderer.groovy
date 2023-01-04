package com.jessebrault.ssg.pagetemplate


import groovy.text.GStringTemplateEngine
import groovy.text.TemplateEngine

class GspRenderer implements PageRenderer {

    private static final TemplateEngine engine = new GStringTemplateEngine()

    @Override
    String render(PageTemplate template, String text) {
        engine.createTemplate(template.file.text).make([
                text: text
        ])
    }

}
