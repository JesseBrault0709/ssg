package com.jessebrault.ssg.di

import com.jessebrault.ssg.model.Model
import groowt.util.di.QualifierHandler
import groowt.util.di.QualifierHandlerContainer
import groowt.util.di.RegistryExtension

import java.lang.annotation.Annotation

class ModelsExtension implements QualifierHandlerContainer, RegistryExtension {

    final Set<Model> allModels = []

    private final QualifierHandler<InjectModel> injectModelQualifierHandler = new InjectModelQualifierHandler(this)
    private final QualifierHandler<InjectModels> injectModelsQualifierHandler = new InjectModelsQualifierHandler(this)

    @Override
    <A extends Annotation> QualifierHandler<A> getQualifierHandler(Class<A> aClass) {
        if (aClass == InjectModel) {
            return this.injectModelQualifierHandler as QualifierHandler<A>
        } else if (aClass == InjectModels) {
            return this.injectModelsQualifierHandler as QualifierHandler<A>
        } else {
            return null
        }
    }

}
