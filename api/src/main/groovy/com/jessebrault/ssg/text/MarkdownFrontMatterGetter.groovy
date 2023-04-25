package com.jessebrault.ssg.text

import com.jessebrault.ssg.util.Diagnostic
import com.jessebrault.ssg.util.Result
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import org.commonmark.ext.front.matter.YamlFrontMatterExtension
import org.commonmark.ext.front.matter.YamlFrontMatterVisitor
import org.commonmark.parser.Parser

@NullCheck
@EqualsAndHashCode
final class MarkdownFrontMatterGetter implements FrontMatterGetter {

    private static final Parser parser = Parser.builder()
            .extensions([YamlFrontMatterExtension.create()])
            .build()

    @Override
    Result<FrontMatter> get(Text text) {
        try {
            def node = parser.parse(text.text)
            def v = new YamlFrontMatterVisitor()
            node.accept(v)
            Result.of(new FrontMatter(text, v.data))
        } catch (Exception e) {
            Result.of(
                    [new Diagnostic(
                            "An exception occured while parsing frontMatter for ${ text.path }:\n${ e }",
                            e
                    )],
                    new FrontMatter(text, [:])
            )
        }
    }

    @Override
    String toString() {
        "MarkdownFrontMatterGetter()"
    }

}
