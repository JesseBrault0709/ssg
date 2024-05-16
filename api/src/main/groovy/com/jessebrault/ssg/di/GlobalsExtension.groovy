package com.jessebrault.ssg.di

import groovy.transform.TupleConstructor
import groowt.util.di.Binding
import groowt.util.di.QualifierHandler
import groowt.util.di.QualifierHandlerContainer
import groowt.util.di.RegistryExtension
import groowt.util.di.SingletonBinding

import java.lang.annotation.Annotation

class GlobalsExtension implements QualifierHandlerContainer, RegistryExtension {

    @TupleConstructor(includeFields = true)
    static class GlobalQualifierHandler implements QualifierHandler<Global> {

        private final GlobalsExtension extension

        @Override
        <T> Binding<T> handle(Global global, Class<T> aClass) {
            if (extension.globals.containsKey(global.value())) {
                return new SingletonBinding<T>(extension.globals.get(global.value()) as T)
            } else {
                throw new IllegalArgumentException("There is no global for ${global.value()}")
            }
        }

    }

    final Map<String, Object> globals = [:]

    private final GlobalQualifierHandler globalQualifierHandler = new GlobalQualifierHandler(this)

    @Override
    <A extends Annotation> QualifierHandler<A> getQualifierHandler(Class<A> aClass) {
        if (Global.is(aClass)) {
            return this.globalQualifierHandler as QualifierHandler<A>
        } else {
            return null
        }
    }

}
