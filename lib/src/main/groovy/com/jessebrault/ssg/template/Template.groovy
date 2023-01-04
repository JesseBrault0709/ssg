package com.jessebrault.ssg.template

import groovy.transform.Canonical

@Canonical
class Template {

    enum Type {
        GSP('.gsp');

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

}
