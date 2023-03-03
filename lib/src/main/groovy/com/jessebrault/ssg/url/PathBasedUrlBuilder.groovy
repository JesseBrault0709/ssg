package com.jessebrault.ssg.url

import java.nio.file.Path

class PathBasedUrlBuilder implements UrlBuilder {

    private final String absolute
    private final String baseUrl
    private final Path fromDirectory

    PathBasedUrlBuilder(String targetPath, String baseUrl) {
        this.absolute = baseUrl + '/' + targetPath
        this.baseUrl = baseUrl
        def fromFilePath = Path.of(targetPath)
        if (fromFilePath.parent) {
            this.fromDirectory = fromFilePath.parent
        } else {
            this.fromDirectory = Path.of('')
        }
    }

    @Override
    String getAbsolute() {
        this.absolute
    }

    @Override
    String absolute(String to) {
        this.baseUrl + '/' + to
    }

    @Override
    String relative(String to) {
        this.fromDirectory.relativize(Path.of(to)).toString()
    }

}
