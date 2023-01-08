package com.jessebrault.ssg.text

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@TupleConstructor(includeFields = true, defaults = false)
@NullCheck
@EqualsAndHashCode(includeFields = true)
class FrontMatter {

    private static final Logger logger = LoggerFactory.getLogger(FrontMatter)

    private final Text text
    private final Map<String, List<String>> data

    String get(String key) {
        if (data[key] != null) {
            data[key][0]
        } else {
            logger.warn('in {} no entry for key {} in frontMatter, returning empty string', this.text, key)
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
            logger.warn('in {} no entry for key {} in frontMatter, returning empty list: {}', this.text, key)
            []
        }
    }

    @Override
    String toString() {
        "FrontMatter(text: ${ this.text }, data: ${ this.data })"
    }

}
