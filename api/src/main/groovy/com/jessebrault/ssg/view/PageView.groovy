package com.jessebrault.ssg.view

import groowt.view.View

interface PageView extends View {
    String getPageTitle()
    void setPageTitle(String pageTitle)

    String getUrl()
    void setUrl(String url)
}
