package com.jessebrault.ssg.text

import com.jessebrault.ssg.Diagnostic
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import org.commonmark.ext.front.matter.YamlFrontMatterExtension
import org.commonmark.ext.front.matter.YamlFrontMatterVisitor
import org.commonmark.parser.Parser

@NullCheck
@EqualsAndHashCode
class MarkdownFrontMatterGetter implements FrontMatterGetter {

    private static final Parser parser = Parser.builder()
            .extensions([YamlFrontMatterExtension.create()])
            .build()

    @Override
    Tuple2<Collection<Diagnostic>, FrontMatter> get(Text text) {
        try {
            def node = parser.parse(text.text)
            def v = new YamlFrontMatterVisitor()
            node.accept(v)
            new Tuple2([], new FrontMatter(v.data))
        } catch (Exception e) {
            new Tuple2<>([new Diagnostic("An exception occured while parsing frontMatter for ${ text.path }:\n${ e }", e)], new FrontMatter([:]))
        }
    }

    @Override
    String toString() {
        "MarkdownFrontMatterGetter()"
    }

}
