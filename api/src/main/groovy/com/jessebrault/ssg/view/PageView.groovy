package com.jessebrault.ssg.view

import groowt.view.View

interface PageView extends View {
    String getPageTitle()
    String getUrl()
}
