package com.jessebrault.ssg.di

import groovy.transform.TupleConstructor
import groowt.util.di.Binding
import groowt.util.di.QualifierHandler
import groowt.util.di.SingletonBinding

@TupleConstructor(includeFields = true)
class InjectModelsQualifierHandler implements QualifierHandler<InjectModels> {

    private final ModelsExtension extension

    @Override
    <T> Binding<T> handle(InjectModels injectModels, Class<T> requestedType) {
        if (!List.is(requestedType)) {
            throw new IllegalArgumentException("@InjectModels must be used with List.")
        }
        def allFound = this.extension.allModels.inject([] as T) { acc, model ->
            if (model.type.isAssignableFrom(requestedType) && model.name in injectModels.value()) {
                acc << model.get()
            }
            acc
        }
        new SingletonBinding<T>(allFound as T)
    }

}
