package com.jessebrault.ssg.html

import com.jessebrault.ssg.page.Page
import com.jessebrault.ssg.provider.CollectionProvider
import com.jessebrault.ssg.provider.CollectionProviders
import com.jessebrault.ssg.task.TaskSpec
import com.jessebrault.ssg.util.ExtensionUtil
import groovy.transform.NullCheck

import java.util.function.Function

@NullCheck
final class PageToHtmlSpecProviders {

    static CollectionProvider<PageToHtmlSpec> from(CollectionProvider<Page> pagesProvider) {
        CollectionProviders.fromCollection(pagesProvider.provide().collect { Page page ->
            new PageToHtmlSpec(page, { TaskSpec taskSpec ->
                ExtensionUtil.stripExtension(page.path) + '.html'
            })
        })
    }

    static CollectionProvider<PageToHtmlSpec> from(
            CollectionProvider<Page> pagesProvider,
            Function<Page, Function<TaskSpec, String>> toRelativeHtmlPath
    ) {
        CollectionProviders.fromCollection(pagesProvider.provide().collect {
            new PageToHtmlSpec(it, toRelativeHtmlPath.apply(it))
        })
    }

    private PageToHtmlSpecProviders() {}

}
