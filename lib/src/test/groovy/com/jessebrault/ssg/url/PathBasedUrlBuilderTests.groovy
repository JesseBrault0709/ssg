package com.jessebrault.ssg.url

class PathBasedUrlBuilderTests extends AbstractUrlBuilderTests {

    @Override
    protected UrlBuilder getUrlBuilder(String fromFile) {
        new PathBasedUrlBuilder(fromFile)
    }

}
