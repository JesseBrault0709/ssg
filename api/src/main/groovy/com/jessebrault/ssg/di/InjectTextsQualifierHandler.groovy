package com.jessebrault.ssg.di

import com.jessebrault.ssg.text.Text
import com.jessebrault.ssg.util.Glob
import groovy.transform.TupleConstructor
import groowt.util.di.Binding
import groowt.util.di.QualifierHandler
import groowt.util.di.SingletonBinding

@TupleConstructor(includeFields = true)
class InjectTextsQualifierHandler implements QualifierHandler<InjectTexts> {

    private final TextsExtension extension

    @Override
    <T> Binding<T> handle(InjectTexts injectTexts, Class<T> aClass) {
        if (!Set.is(aClass)) {
            throw new IllegalArgumentException('Cannot @InjectTexts on a non-Set parameter/method/field.')
        }
        def allFound = injectTexts.value().inject([] as Set<Text>) { acc, nameOrPathGlob ->
            if (nameOrPathGlob.startsWith('/')) {
                def glob = new Glob(nameOrPathGlob)
                def matching = this.extension.allTexts.inject([] as Set<Text>) { matchingAcc, text ->
                    if (glob.matches(text.path)) {
                        matchingAcc << text
                    }
                    matchingAcc
                }
                acc.addAll(matching)
            } else {
                def found = this.extension.allTexts.find { it.name == nameOrPathGlob }
                acc << found
            }
            acc
        }
        new SingletonBinding<T>(allFound as T)
    }

}
