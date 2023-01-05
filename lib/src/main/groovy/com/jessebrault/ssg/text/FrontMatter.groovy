package com.jessebrault.ssg.text

import groovy.transform.NullCheck
import groovy.transform.ToString
import groovy.transform.TupleConstructor
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@TupleConstructor(includeFields = true, defaults = false)
@NullCheck
@ToString(includeFields = true)
class FrontMatter {

    private static final Logger logger = LoggerFactory.getLogger(FrontMatter)

    private final Map<String, List<String>> data

    String get(String key) {
        if (data[key] != null) {
            data[key][0]
        } else {
            logger.warn('no entry for key in frontMatter, returning empty string: {}', key)
            ''
        }
    }

    String getAt(String key) {
        this.get(key)
    }

    List<String> getList(String key) {
        if (data[key] != null) {
            data[key]
        } else {
            logger.warn('no entry for key in frontMatter, returning empty list: {}', key)
            []
        }
    }

}
