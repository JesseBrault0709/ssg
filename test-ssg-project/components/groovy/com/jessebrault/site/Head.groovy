package com.jessebrault.site

import groowt.view.component.web.BaseWebViewComponent

class Head extends BaseWebViewComponent {

    final String title

    Head(Map attr) {
        super(Head.getResource('HeadTemplate.wvc'))
        this.title = attr.title()
    }

}
