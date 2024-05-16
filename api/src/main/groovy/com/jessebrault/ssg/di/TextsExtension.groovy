package com.jessebrault.ssg.di

import com.jessebrault.ssg.text.Text
import groowt.util.di.QualifierHandler
import groowt.util.di.QualifierHandlerContainer
import groowt.util.di.RegistryExtension

import java.lang.annotation.Annotation

class TextsExtension implements QualifierHandlerContainer, RegistryExtension {

    final Set<Text> allTexts = []

    private final QualifierHandler<InjectText> injectTextQualifierHandler = new InjectTextQualifierHandler(this)
    private final QualifierHandler<InjectTexts> injectTextsQualifierHandler = new InjectTextsQualifierHandler(this)

    @Override
    <A extends Annotation> QualifierHandler<A> getQualifierHandler(Class<A> aClass) {
        if (InjectText.is(aClass)) {
            return this.injectTextQualifierHandler as QualifierHandler<A>
        } else if (InjectTexts.is(aClass)) {
            return this.injectTextsQualifierHandler as QualifierHandler<A>
        } else {
            return null
        }
    }

}
