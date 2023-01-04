package com.jessebrault.ssg.textfile

import groovy.transform.Canonical

@Canonical
class TextFile {

    enum Type {
        MARKDOWN('.md');

        private final String extension

        Type(String extension) {
            this.extension = extension
        }

        String getExtension() {
            this.extension
        }

    }

    File file
    String relativePath
    Type type

}
