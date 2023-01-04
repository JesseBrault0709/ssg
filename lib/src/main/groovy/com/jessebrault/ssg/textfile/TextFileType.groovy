package com.jessebrault.ssg.textfile

import com.jessebrault.ssg.textrenderer.TextRenderer
import groovy.transform.Canonical

@Canonical
class TextFileType {
    Collection<String> extensions
    TextRenderer renderer
}
