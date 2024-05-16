package com.jessebrault.ssg.di

import com.jessebrault.ssg.page.Page
import com.jessebrault.ssg.util.Glob
import groovy.transform.TupleConstructor
import groowt.util.di.Binding
import groowt.util.di.QualifierHandler
import groowt.util.di.SingletonBinding

@TupleConstructor(includeFields = true)
class InjectPagesQualifierHandler implements QualifierHandler<InjectPages> {

    private final PagesExtension pagesExtension

    @Override
    <T> Binding<T> handle(InjectPages injectPages, Class<T> requestedClass) {
        if (!(Set.isAssignableFrom(requestedClass))) {
            throw new IllegalArgumentException(
                    'Cannot inject a Collection of Pages into a non-Collection parameter/setter/field.'
            )
        }
        def foundPages = [] as Set<Page>
        for (final String requested : injectPages.value()) {
            if (requested.startsWith('/')) {
                def glob = new Glob(requested)
                def allFound = this.pagesExtension.allPages.inject([] as Set<Page>) { acc, page ->
                    if (glob.matches(page.path)) {
                        acc << page
                    }
                    acc
                }
                allFound.each { foundPages << it }
            } else {
                def found = this.pagesExtension.allPages.find { it.name == requested }
                if (found == null) {
                    throw new IllegalArgumentException("Cannot find page with the name: $requested")
                }
                foundPages << found
            }
        }
        new SingletonBinding<T>(foundPages as T)
    }

}
