package com.jessebrault.ssg.provider

import com.jessebrault.ssg.page.Page
import groovy.transform.TupleConstructor
import groowt.util.fp.provider.NamedProvider
import groowt.util.fp.provider.Provider

@TupleConstructor(includeFields = true, force = true, defaults = false)
class PageProvider implements NamedProvider<Page> {

    final String name
    final String path

    private final Provider<Page> lazyPage

    PageProvider(String name, String path, Page page) {
        this.name = name
        this.path = path
        this.lazyPage = { page }
    }

    @Override
    Page get() {
        this.lazyPage.get()
    }

}
