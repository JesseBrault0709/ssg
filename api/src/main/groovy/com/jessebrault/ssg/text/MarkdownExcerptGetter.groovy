package com.jessebrault.ssg.text

import com.jessebrault.ssg.util.Diagnostic
import com.jessebrault.ssg.util.Result
import org.commonmark.ext.front.matter.YamlFrontMatterExtension
import org.commonmark.node.AbstractVisitor
import org.commonmark.parser.Parser

final class MarkdownExcerptGetter implements ExcerptGetter {

    private static class ExcerptVisitor extends AbstractVisitor {

        final int limit
        List<String> words = []

        ExcerptVisitor(int limit) {
            this.limit = limit
        }

        @Override
        void visit(org.commonmark.node.Text text) {
            if (this.words.size() <= limit) {
                def textWords = text.literal.split('\\s+').toList()
                def taken = textWords.take(this.limit - this.words.size())
                this.words.addAll(taken)
            }
        }

        String getResult() {
            this.words.take(this.limit).join(' ')
        }

    }

    private static final Parser parser = Parser.builder()
            .extensions([YamlFrontMatterExtension.create()])
            .build()

    @Override
    Result<String> getExcerpt(Text text, int limit) {
        try {
            def node = parser.parse(text.text)
            def visitor = new ExcerptVisitor(limit)
            node.accept(visitor)
            Result.of(visitor.result)
        } catch (Exception e) {
            def diagnostic = new Diagnostic(
                    "There was an exception while getting the excerpt for ${ text }:\n${ e }",
                    e
            )
            Result.of([diagnostic], '')
        }
    }

}
