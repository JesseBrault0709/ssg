package com.jessebrault.ssg.page

import groovy.transform.NullCheck

@NullCheck
final class PageTypes {

    static PageType getGsp(Collection<String> extensions, ClassLoader parentClassLoader) {
        new PageType(extensions, new GspPageRenderer(parentClassLoader))
    }

    private PageTypes() {}

}
