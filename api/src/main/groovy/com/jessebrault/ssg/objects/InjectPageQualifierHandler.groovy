package com.jessebrault.ssg.objects

import com.jessebrault.ssg.page.Page
import groovy.transform.TupleConstructor
import groowt.util.di.Binding
import groowt.util.di.QualifierHandler
import groowt.util.di.SingletonBinding

@TupleConstructor(includeFields = true)
class InjectPageQualifierHandler implements QualifierHandler<InjectPage> {

    private final PagesExtension pagesExtension

    @Override
    <T> Binding<T> handle(InjectPage injectPage, Class<T> requestedClass) {
        if (!Page.isAssignableFrom(requestedClass)) {
            throw new IllegalArgumentException("Cannot inject a Page into a non-Page parameter/setter/field.")
        }
        def requested = injectPage.value()
        def found = this.pagesExtension.pageProviders.find {
            if (requested.startsWith('/')) {
                it.path == requested
            } else {
                it.name == requested
            }
        }
        if (found == null) {
            throw new IllegalArgumentException("Cannot find a page with the following name or path: $requested")
        }
        new SingletonBinding<T>(found.get() as T)
    }

}
