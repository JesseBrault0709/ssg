package com.jessebrault.ssg.di

import com.jessebrault.ssg.page.Page
import groovy.transform.TupleConstructor
import groowt.util.di.*

import java.lang.annotation.Annotation

class SelfPageExtension implements RegistryExtension, QualifierHandlerContainer {

    @TupleConstructor(includeFields = true)
    static class SelfPageQualifierHandler implements QualifierHandler<SelfPage> {

        private final SelfPageExtension extension

        @Override
        <T> Binding<T> handle(SelfPage selfPage, Class<T> requestedType) {
            if (!Page.class.isAssignableFrom(requestedType)) {
                throw new IllegalArgumentException('Cannot put @SelfPage on a non-Page parameter/method/field.')
            }
            if (this.extension.currentPage == null) {
                throw new IllegalStateException('Cannot get @SelfPage because extension.currentPage is null.')
            }
            new SingletonBinding<T>(this.extension.currentPage as T)
        }

    }

    Page currentPage

    private final SelfPageQualifierHandler selfPageQualifierHandler = new SelfPageQualifierHandler(this)

    @Override
    <A extends Annotation> QualifierHandler<A> getQualifierHandler(Class<A> annotationType) {
        if (SelfPage.is(annotationType)) {
            return this.selfPageQualifierHandler as QualifierHandler<A>
        } else {
            return null
        }
    }

}
