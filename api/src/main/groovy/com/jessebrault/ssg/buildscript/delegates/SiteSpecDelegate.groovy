package com.jessebrault.ssg.buildscript.delegates

import com.jessebrault.ssg.SiteSpec
import com.jessebrault.ssg.util.Zero

import static java.util.Objects.requireNonNull

final class SiteSpecDelegate {

    private String name
    private String baseUrl

    SiteSpecDelegate(Zero<SiteSpec> siteSpecZero) {
        this.name = siteSpecZero.zero.name
        this.baseUrl = siteSpecZero.zero.baseUrl
    }

    String getName() {
        return this.name
    }

    void setName(String name) {
        this.name = requireNonNull(name)
    }

    String getBaseUrl() {
        return this.baseUrl
    }

    void setBaseUrl(String baseUrl) {
        this.baseUrl = requireNonNull(baseUrl)
    }

    SiteSpec getResult() {
        new SiteSpec(this.name, this.baseUrl)
    }

}
