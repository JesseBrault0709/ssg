package com.jessebrault.ssg.buildscript.dsl

import com.jessebrault.ssg.SiteSpec

final class SiteSpecDelegate {

    String name
    String baseUrl

    SiteSpecDelegate() {
        def blank = SiteSpec.getBlank()
        this.name = blank.name
        this.baseUrl = blank.baseUrl
    }

    SiteSpec getResult() {
        new SiteSpec(this.name, this.baseUrl)
    }

}
