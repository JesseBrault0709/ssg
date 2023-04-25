package com.jessebrault.ssg.page

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@TupleConstructor(defaults = false)
@NullCheck(includeGenerated = true)
@EqualsAndHashCode
final class PageType {

    Collection<String> ids
    PageRenderer renderer

    @Override
    String toString() {
        "PageType(ids: ${ this.ids }, renderer: ${ this.renderer })"
    }

}
