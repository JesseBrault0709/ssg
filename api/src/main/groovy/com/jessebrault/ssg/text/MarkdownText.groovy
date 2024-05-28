package com.jessebrault.ssg.text

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.yaml.YamlSlurper
import org.commonmark.ext.front.matter.YamlFrontMatterExtension
import org.commonmark.node.AbstractVisitor
import org.commonmark.node.Node
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

@NullCheck(includeGenerated = true)
@EqualsAndHashCode
class MarkdownText implements Text {

    protected static class MarkdownExcerptVisitor extends AbstractVisitor {

        private final int length
        private final List<String> words

        MarkdownExcerptVisitor(int length) {
            this.length = length
            this.words = []
        }

        private int getCurrentLength() {
            this.words.inject(0) { acc, word -> acc + word.length() }
        }

        @Override
        void visit(org.commonmark.node.Text text) {
            if (this.currentLength < length) {
                def words = text.literal.split('\\s+').toList()
                def wordsIter = words.iterator()
                while (wordsIter.hasNext() && this.currentLength < length) {
                    def word = wordsIter.next()
                    if (word.length() > this.length - this.currentLength) {
                        break
                    } else {
                        //noinspection GroovyVariableNotAssigned -- this is certainly an IntelliJ bug
                        this.words << word
                    }
                }
            }
        }

        String getResult() {
            this.words.join(' ')
        }

    }

    private static final Parser markdownParser = Parser.builder()
            .extensions(List.of(YamlFrontMatterExtension.create()))
            .build()

    private static final HtmlRenderer htmlRenderer = HtmlRenderer.builder().build()

    final String name
    final String path

    private final File source

    private boolean didInit
    private Object frontMatter
    private Node parsed

    MarkdownText(String name, String path, File source) {
        this.name = name
        this.path = path
        this.source = source
    }

    private void initFrontMatter(String completeSourceText) {
        if (completeSourceText.startsWith('---')) {
            def delimiterIndex = completeSourceText.indexOf('---', 3)
            def frontMatter = completeSourceText.substring(3, delimiterIndex)
            this.frontMatter = new YamlSlurper().parseText(frontMatter)
        } else {
            this.frontMatter = [:]
        }
    }

    private void initParsed(String completeSourceText) {
        if (completeSourceText.startsWith('---')) {
            def delimiterIndex = completeSourceText.indexOf('---', 3)
            def body = completeSourceText.substring(delimiterIndex + 3)
            if (body.startsWith('\n')) {
                this.parsed = markdownParser.parse(body.substring(1))
            } else {
                this.parsed = markdownParser.parse(body)
            }
        } else {
            this.parsed = markdownParser.parse(completeSourceText)
        }
    }

    private void init() {
        if (!this.didInit) {
            def completeSourceText = this.source.text
            this.initFrontMatter(completeSourceText)
            this.initParsed(completeSourceText)
            this.didInit = true
        }
    }

    @Override
    Object getFrontMatter() {
        this.init()
        this.frontMatter
    }

    @Override
    String getExcerpt(int length) {
        this.init()
        def v = new MarkdownExcerptVisitor(length)
        this.parsed.accept(v)
        v.result
    }

    @Override
    void renderTo(Writer writer) {
        this.init()
        htmlRenderer.render(this.parsed, writer)
    }

    @Override
    String toString() {
        "MarkdownText(name: ${this.name}, path: ${this.path})"
    }

}
