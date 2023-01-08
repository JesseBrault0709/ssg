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
    String render(String text, Collection<Text> texts, Collection<Part> parts, Map globals) {
        Objects.requireNonNull(text)
        Objects.requireNonNull(texts)
        Objects.requireNonNull(parts)
        Objects.requireNonNull(globals)

        engine.createTemplate(text).make([
                globals: globals,
                parts: new EmbeddablePartsMap(parts, globals),
                texts: new EmbeddableTextsCollection(texts, globals)
        ])
    }

}
