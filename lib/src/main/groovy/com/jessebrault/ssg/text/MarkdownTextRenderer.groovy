package com.jessebrault.ssg.text

import groovy.transform.EqualsAndHashCode
import org.commonmark.ext.front.matter.YamlFrontMatterExtension
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

@EqualsAndHashCode
class MarkdownTextRenderer implements TextRenderer {

    private static final Parser parser = Parser.builder()
            .extensions([YamlFrontMatterExtension.create()])
            .build()
    private static final HtmlRenderer htmlRenderer = HtmlRenderer.builder().build()

    @Override
    String render(String text, Map globals) {
        htmlRenderer.render(parser.parse(text))
    }

    @Override
    String toString() {
        "MarkdownTextRenderer()"
    }

}
