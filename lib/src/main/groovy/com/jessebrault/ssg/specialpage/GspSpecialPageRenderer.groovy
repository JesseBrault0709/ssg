package com.jessebrault.ssg.specialpage

import com.jessebrault.ssg.part.Part
import com.jessebrault.ssg.part.EmbeddablePartsMap
import com.jessebrault.ssg.text.EmbeddableTextsCollection
import com.jessebrault.ssg.text.Text
import groovy.text.GStringTemplateEngine
import groovy.text.TemplateEngine

class GspSpecialPageRenderer implements SpecialPageRenderer {

    private static final TemplateEngine engine = new GStringTemplateEngine()

    @Override
    String render(String text, Collection<Text> texts, Collection<Part> parts) {
        engine.createTemplate(text).make([
                texts: new EmbeddableTextsCollection(texts),
                parts: new EmbeddablePartsMap(parts)
        ])
    }

}
