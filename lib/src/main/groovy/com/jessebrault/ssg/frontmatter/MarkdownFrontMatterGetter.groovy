package com.jessebrault.ssg.frontmatter

import org.commonmark.ext.front.matter.YamlFrontMatterExtension
import org.commonmark.ext.front.matter.YamlFrontMatterVisitor
import org.commonmark.parser.Parser

class MarkdownFrontMatterGetter implements FrontMatterGetter {

    private static final Parser parser = Parser.builder()
            .extensions([YamlFrontMatterExtension.create()])
            .build()

    @Override
    FrontMatter get(String text) {
        def node = parser.parse(text)
        def v = new YamlFrontMatterVisitor()
        node.accept(v)
        new FrontMatter(v.data)
    }

}
