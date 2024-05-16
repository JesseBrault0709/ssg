package com.jessebrault.ssg.di

import com.jessebrault.ssg.text.Text
import groovy.transform.TupleConstructor
import groowt.util.di.Binding
import groowt.util.di.QualifierHandler
import groowt.util.di.SingletonBinding

@TupleConstructor(includeFields = true)
class InjectTextQualifierHandler implements QualifierHandler<InjectText> {

    private final TextsExtension extension

    @Override
    <T> Binding<T> handle(InjectText injectText, Class<T> requestedClass) {
        if (!Text.isAssignableFrom(requestedClass)) {
            throw new IllegalArgumentException("Cannot @InjectText on a non-Text parameter/method/field.")
        }
        def found = this.extension.allTexts.find {
            it.name == injectText.value() || it.path == injectText.value()
        }
        if (found == null) {
            throw new IllegalArgumentException("Could not find a Text with name or path ${injectText.value()}")
        }
        new SingletonBinding<T>(found as T)
    }

}
