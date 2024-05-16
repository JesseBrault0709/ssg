package com.jessebrault.ssg.text

interface Text {

    String getName()
    String getPath()
    Object getFrontMatter()
    String getExcerpt(int length)

    void renderTo(Writer writer)

    default String render() {
        def w = new StringWriter()
        this.renderTo(w)
        w.toString()
    }

}
