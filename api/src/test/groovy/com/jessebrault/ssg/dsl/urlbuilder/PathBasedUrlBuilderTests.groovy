package com.jessebrault.ssg.dsl.urlbuilder

final class PathBasedUrlBuilderTests extends AbstractUrlBuilderTests {

    @Override
    protected UrlBuilder getUrlBuilder(String targetPath, String baseUrl) {
        new PathBasedUrlBuilder(targetPath, baseUrl)
    }

}
