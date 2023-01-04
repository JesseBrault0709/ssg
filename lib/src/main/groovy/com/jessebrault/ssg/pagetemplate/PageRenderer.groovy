package com.jessebrault.ssg.pagetemplate

interface PageRenderer {
    String render(PageTemplate template, String text)
}
