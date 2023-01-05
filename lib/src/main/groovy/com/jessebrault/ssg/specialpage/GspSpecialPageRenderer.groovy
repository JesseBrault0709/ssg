package com.jessebrault.ssg.specialpage

import com.jessebrault.ssg.part.Part
import com.jessebrault.ssg.part.PartsMap
import com.jessebrault.ssg.text.Text
import com.jessebrault.ssg.text.TextsMap
import groovy.text.GStringTemplateEngine
import groovy.text.TemplateEngine

class GspSpecialPageRenderer implements SpecialPageRenderer {

    private static final TemplateEngine engine = new GStringTemplateEngine()

    @Override
    String render(String text, Collection<Text> texts, Collection<Part> parts) {
        engine.createTemplate(text).make([
                texts: new TextsMap(texts),
                parts: new PartsMap(parts)
        ])
    }

}
