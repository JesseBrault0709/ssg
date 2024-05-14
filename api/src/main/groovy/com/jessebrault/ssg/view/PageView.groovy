package com.jessebrault.ssg.view

import groowt.view.View

interface PageView extends View {

    String getPageTitle()
    void setPageTitle(String pageName)

    String getUrl()
    void setUrl(String url)

}
