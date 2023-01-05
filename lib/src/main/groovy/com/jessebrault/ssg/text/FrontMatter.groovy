package com.jessebrault.ssg.text

import groovy.transform.NullCheck
import groovy.transform.ToString
import groovy.transform.TupleConstructor

@TupleConstructor(includeFields = true, defaults = false)
@NullCheck
@ToString
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
