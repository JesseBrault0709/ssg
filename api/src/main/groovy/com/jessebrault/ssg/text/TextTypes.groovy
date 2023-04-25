package com.jessebrault.ssg.text

final class TextTypes {

    static final MARKDOWN = new TextType(
            ['.md'],
            new MarkdownTextRenderer(),
            new MarkdownFrontMatterGetter(),
            new MarkdownExcerptGetter()
    )

    private TextTypes() {}

}
