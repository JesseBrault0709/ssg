package com.jessebrault.ssg.url

class PathBasedUrlBuilderTests extends AbstractUrlBuilderTests {

    @Override
    protected UrlBuilder getUrlBuilder(String targetPath) {
        new PathBasedUrlBuilder(targetPath)
    }

}
