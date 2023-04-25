package com.jessebrault.ssg.page

final class PageTypes {

    static final PageType GSP = new PageType(['.gsp'], new GspPageRenderer())

    private PageTypes() {}

}
