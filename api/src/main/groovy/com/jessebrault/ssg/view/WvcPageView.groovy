package com.jessebrault.ssg.view

import groowt.view.web.BaseWebViewComponent

class WvcPageView extends BaseWebViewComponent implements PageView, WithHtmlHelpers {

    String pageTitle
    String url

    WvcPageView(Object source) {
        super(source)
    }

    @Override
    void renderTo(Writer out) throws IOException {
        def sw = new StringWriter()
        super.renderTo(sw)
        out.write(this.prettyFormat(sw.toString()))
    }
    
}
