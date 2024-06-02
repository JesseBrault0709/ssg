package com.jessebrault.ssg.page

abstract class AbstractPage implements Page {

    final String name
    final String path
    final String fileExtension

    AbstractPage(Map args) {
        name = args.name
        path = args.path
        fileExtension = args.fileExtension
    }

    @Override
    int hashCode() {
        Objects.hash(name, path, fileExtension)
    }

    @Override
    boolean equals(Object obj) {
        if (this.is(obj)) {
            return true
        } else if (obj instanceof Page) {
            return name == obj.name
                    && path == obj.path
                    && fileExtension == obj.fileExtension
        } else {
            return false
        }
    }

}
