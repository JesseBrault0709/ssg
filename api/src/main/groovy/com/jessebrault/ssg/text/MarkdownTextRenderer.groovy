package com.jessebrault.ssg.text

import com.jessebrault.ssg.util.Diagnostic
import com.jessebrault.ssg.util.Result
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import org.commonmark.ext.front.matter.YamlFrontMatterExtension
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

@NullCheck
@EqualsAndHashCode
final class MarkdownTextRenderer implements TextRenderer {

    private static final Parser parser = Parser.builder()
            .extensions([YamlFrontMatterExtension.create()])
            .build()
    private static final HtmlRenderer htmlRenderer = HtmlRenderer.builder().build()

    @Override
    Result<String> render(Text text) {
        try {
            Result.of(htmlRenderer.render(parser.parse(text.text)))
        } catch (Exception e) {
            Result.of(
                    [new Diagnostic("There was an exception while rendering ${ text.path }:\n${ e }", e)],
                    ''
            )
        }
    }

    @Override
    String toString() {
        "MarkdownTextRenderer()"
    }

}
