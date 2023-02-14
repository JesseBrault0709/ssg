package com.jessebrault.ssg.url

import java.nio.file.Path

class PathBasedUrlBuilder implements UrlBuilder {

    private final Path fromDirectory

    PathBasedUrlBuilder(String fromFile) {
        def fromFilePath = Path.of(fromFile)
        if (fromFilePath.parent) {
            this.fromDirectory = fromFilePath.parent
        } else {
            this.fromDirectory = Path.of('')
        }
    }

    @Override
    String relative(String to) {
        this.fromDirectory.relativize(Path.of(to)).toString()
    }

}
