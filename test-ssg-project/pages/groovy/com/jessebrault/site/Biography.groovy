package com.jessebrault.site

import com.jessebrault.ssg.di.InjectText
import com.jessebrault.ssg.page.PageSpec
import com.jessebrault.ssg.text.Text
import com.jessebrault.ssg.view.WvcPageView
import jakarta.inject.Inject

@PageSpec(name = 'Biography', path = '/biography')
class Biography extends WvcPageView {

    static final String greeting = 'Hello, World!'

    final Text biographyText

    @Inject
    Biography(@InjectText('/Biography.md') Text biographyText) {
        super(Biography.getResource('BiographyTemplate.wvc'))
        this.biographyText = biographyText
    }

}
