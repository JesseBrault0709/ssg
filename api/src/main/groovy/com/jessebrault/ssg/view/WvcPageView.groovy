package com.jessebrault.ssg.view

import groowt.view.web.BaseWebViewComponent

class WvcPageView extends BaseWebViewComponent implements PageView {

    String pageTitle
    String url

    WvcPageView(Object source) {
        super(source)
    }

}
