package com.jessebrault.ssg.template

import com.jessebrault.ssg.part.Part
import com.jessebrault.ssg.part.PartsMap
import com.jessebrault.ssg.text.FrontMatter
import groovy.text.GStringTemplateEngine
import groovy.text.TemplateEngine

class GspTemplateRenderer implements TemplateRenderer {

    private static final TemplateEngine engine = new GStringTemplateEngine()

    @Override
    String render(Template template, FrontMatter frontMatter, String text, Collection<Part> parts) {
        engine.createTemplate(template.text).make([
                frontMatter: frontMatter,
                text: text,
                parts: new PartsMap(parts)
        ])
    }

}
