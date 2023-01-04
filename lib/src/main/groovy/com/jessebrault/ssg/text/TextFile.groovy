package com.jessebrault.ssg.text

import groovy.transform.Canonical

@Canonical
class TextFile {
    File file
    String relativePath
    TextFileType type
}
