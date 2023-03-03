package com.jessebrault.ssg

import groovy.transform.EqualsAndHashCode
import groovy.transform.MapConstructor
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@TupleConstructor(force = true, defaults = false)
@MapConstructor
@NullCheck
@EqualsAndHashCode
final class SiteSpec {

    String name
    String baseUrl

    SiteSpec(SiteSpec source) {
        this.name = source.name
        this.baseUrl = source.baseUrl
    }

    @Override
    String toString() {
        "SiteSpec(${ this.name }, ${ this.baseUrl })"
    }

}
