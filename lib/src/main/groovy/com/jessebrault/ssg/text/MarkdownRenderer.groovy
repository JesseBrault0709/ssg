package com.jessebrault.ssg.text

import org.commonmark.ext.front.matter.YamlFrontMatterExtension
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

class MarkdownRenderer implements TextRenderer {

    private static final Parser parser = Parser.builder()
            .extensions([YamlFrontMatterExtension.create()])
            .build()
    private static final HtmlRenderer htmlRenderer = HtmlRenderer.builder().build()

    @Override
    String render(String text) {
        htmlRenderer.render(parser.parse(text))
    }

}
