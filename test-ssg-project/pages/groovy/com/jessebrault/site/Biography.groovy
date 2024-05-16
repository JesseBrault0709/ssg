package com.jessebrault.site

import com.jessebrault.ssg.page.PageSpec
import com.jessebrault.ssg.view.WvcPageView
import jakarta.inject.Inject

@PageSpec(name = 'Biography', path = '/biography')
class Biography extends WvcPageView {

    @Inject
    Biography() {
        super(Biography.getResource('BiographyTemplate.wvc'))
        println 'Hello from Biography!'
    }

    @Override
    void renderTo(Writer out) throws IOException {
        println "Rendering: $pageTitle..."
        super.renderTo(out)
    }

}
