package com.jessebrault.ssg.di

import groovy.transform.TupleConstructor
import groowt.util.di.Binding
import groowt.util.di.QualifierHandler
import groowt.util.di.SingletonBinding

@TupleConstructor(includeFields = true)
class InjectModelQualifierHandler implements QualifierHandler<InjectModel> {

    private final ModelsExtension extension

    @Override
    <T> Binding<T> handle(InjectModel injectModel, Class<T> requestedClass) {
        def found = this.extension.allModels.find {
            requestedClass.isAssignableFrom(it.class) && it.name == injectModel.value()
        }
        if (found == null) {
            throw new IllegalArgumentException(
                    "Could not find a Model with name ${injectModel.value()} and/or type $requestedClass.name"
            )
        }
        new SingletonBinding<T>(found.get() as T)
    }

}
