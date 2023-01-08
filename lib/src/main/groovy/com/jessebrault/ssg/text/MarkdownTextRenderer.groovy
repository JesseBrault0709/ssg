package com.jessebrault.ssg.text

import com.jessebrault.ssg.Diagnostic
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import org.commonmark.ext.front.matter.YamlFrontMatterExtension
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

@NullCheck
@EqualsAndHashCode
class MarkdownTextRenderer implements TextRenderer {

    private static final Parser parser = Parser.builder()
            .extensions([YamlFrontMatterExtension.create()])
            .build()
    private static final HtmlRenderer htmlRenderer = HtmlRenderer.builder().build()

    @Override
    Tuple2<Collection<Diagnostic>, String> render(Text text, Map globals) {
        try {
            new Tuple2<>([], htmlRenderer.render(parser.parse(text.text)))
        } catch (Exception e) {
            new Tuple2<>([new Diagnostic("There was an exception while rendering ${ text.path }:\n${ e }", e)], '')
        }
    }

    @Override
    String toString() {
        "MarkdownTextRenderer()"
    }

}
