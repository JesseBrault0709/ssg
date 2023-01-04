package com.jessebrault.ssg.text

import groovy.transform.Canonical

@Canonical
class TextFileType {
    Collection<String> extensions
    TextRenderer renderer
    FrontMatterGetter frontMatterGetter
}
