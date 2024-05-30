package com.jessebrault.site

import com.jessebrault.ssg.di.InjectText
import com.jessebrault.ssg.page.PageSpec
import com.jessebrault.ssg.text.Text
import com.jessebrault.ssg.view.WvcPageView
import groowt.view.component.web.WebViewComponentScope
import jakarta.inject.Inject

@PageSpec(name = 'Biography', path = '/biography')
class Biography extends WvcPageView {

    static final String greeting = 'Hello, World!'

    final Text biographyText

    @Inject
    Biography(@InjectText('/Biography.md') Text biographyText) {
        this.biographyText = biographyText
    }

    @Override
    protected void beforeRender() {
        context.configureRootScope(WebViewComponentScope) {
            addWithAttr(Head)
        }
    }

}
