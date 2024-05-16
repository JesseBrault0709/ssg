package com.jessebrault.ssg.view

import groowt.view.StandardGStringTemplateView

abstract class GStringPageView extends StandardGStringTemplateView implements PageView {

    String pageTitle
    String url

    GStringPageView(Map<String, Object> args) {
        super(args)
    }

}
