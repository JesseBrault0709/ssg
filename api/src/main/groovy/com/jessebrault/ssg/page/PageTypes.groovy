package com.jessebrault.ssg.page

final class PageTypes {

    @Deprecated
    static final PageType GSP = new PageType(['.gsp'], new GspPageRenderer(PageTypes.classLoader, []))

    static PageType getGsp(Collection<String> extensions, ClassLoader classLoader, Collection<URL> urls) {
        new PageType(extensions, new GspPageRenderer(classLoader, urls))
    }

    private PageTypes() {}

}
