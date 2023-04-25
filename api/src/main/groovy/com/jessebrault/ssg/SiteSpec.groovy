package com.jessebrault.ssg

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@TupleConstructor(defaults = false)
@NullCheck(includeGenerated = true)
@EqualsAndHashCode
final class SiteSpec {

    static SiteSpec getBlank() {
        new SiteSpec('', '')
    }

    static SiteSpec concat(SiteSpec s0, SiteSpec s1) {
        new SiteSpec(
                s1.name.blank ? s0.name : s1.name,
                s1.baseUrl.blank ? s0.baseUrl : s1.baseUrl
        )
    }

    final String name
    final String baseUrl

    SiteSpec plus(SiteSpec other) {
        concat(this, other)
    }

    @Override
    String toString() {
        "SiteSpec(${ this.name }, ${ this.baseUrl })"
    }

}
