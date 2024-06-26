package com.jessebrault.ssg.di

import com.jessebrault.ssg.page.Page
import groowt.util.di.QualifierHandler
import groowt.util.di.QualifierHandlerContainer
import groowt.util.di.RegistryExtension

import java.lang.annotation.Annotation

class PagesExtension implements QualifierHandlerContainer, RegistryExtension {

    final Set<Page> allPages = []

    private final QualifierHandler<InjectPage> injectPage = new InjectPageQualifierHandler(this)
    private final QualifierHandler<InjectPages> injectPages = new InjectPagesQualifierHandler(this)

    @Override
    <A extends Annotation> QualifierHandler<A> getQualifierHandler(Class<A> annotationClass) {
        if (annotationClass == InjectPage) {
            return this.injectPage as QualifierHandler<A>
        } else if (annotationClass == InjectPages) {
            return this.injectPages as QualifierHandler<A>
        }
        return null
    }

}
