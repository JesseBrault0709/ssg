package com.jessebrault.site

import com.jessebrault.ssg.page.PageSpec
import com.jessebrault.ssg.view.WvcPageView
import jakarta.inject.Inject

@PageSpec(name = 'Biography', path = '/biography')
class Biography extends WvcPageView {

    static final String greeting = 'Hello, World!'

    @Inject
    Biography() {
        super(Biography.getResource('BiographyTemplate.wvc'))
    }

}
