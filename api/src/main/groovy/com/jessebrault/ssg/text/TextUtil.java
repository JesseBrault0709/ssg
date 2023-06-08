package com.jessebrault.ssg.text;

import org.commonmark.ext.front.matter.YamlFrontMatterExtension;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.util.List;

public final class TextUtil {

    private static final Parser parser = Parser.builder()
            .extensions(List.of(YamlFrontMatterExtension.create()))
            .build();

    private static final HtmlRenderer htmlRenderer = HtmlRenderer.builder().build();

    public static String renderMarkdown(String markdown) {
        return htmlRenderer.render(parser.parse(markdown));
    }

    private TextUtil() {}

}
