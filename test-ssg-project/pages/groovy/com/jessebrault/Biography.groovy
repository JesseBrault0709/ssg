package com.jessebrault

import com.jessebrault.ssg.view.WvcPageView
import jakarta.inject.Inject
import jakarta.inject.Named

class Biography extends WvcPageView {

    final String pageTitle
    final String url

    @Inject
    Biography(@Named('siteTitle') String siteTitle, @Named('baseUrl') String baseUrl) {
        super(Biography.getResource('BiographyTemplate.wvc'))
        this.pageTitle = siteTitle + ': Biography'
        this.url = baseUrl + '/biography'
    }

}
