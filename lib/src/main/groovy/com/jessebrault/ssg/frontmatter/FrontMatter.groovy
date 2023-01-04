package com.jessebrault.ssg.frontmatter

import groovy.transform.TupleConstructor

@TupleConstructor(includeFields = true)
class FrontMatter {

    private final Map<String, List<String>> data

    String getSingle(String key) {
        data[key][0]
    }

    String getAt(String key) {
        this.getSingle(key)
    }

    List<String> getList(String key) {
        data[key]
    }

}
