package com.jessebrault.ssg.buildscript

import com.jessebrault.ssg.SiteSpec

class SiteSpecClosureDelegate {

    @Delegate
    private final SiteSpec siteSpec

    SiteSpecClosureDelegate(SiteSpec siteSpec) {
        this.siteSpec = siteSpec
    }

}
