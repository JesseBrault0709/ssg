package com.jessebrault.ssg.output

import com.jessebrault.ssg.input.InputPage
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck

import static com.jessebrault.ssg.util.ExtensionsUtil.stripExtension

@NullCheck
@EqualsAndHashCode
final class OutputPage {

    static OutputPage of(InputPage inputFile, String extension, String content) {
        new OutputPage(stripExtension(inputFile.path) + extension, content)
    }

    final String path
    final String content

    private OutputPage(String path, String content) {
        this.path = path
        this.content = content
    }

    @Override
    String toString() {
        "GeneratedPage(path: ${ this.path })"
    }

}
