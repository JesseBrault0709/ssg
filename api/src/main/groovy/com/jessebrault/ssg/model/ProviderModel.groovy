package com.jessebrault.ssg.model

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.PackageScope
import groovy.transform.TupleConstructor
import groowt.util.fp.provider.Provider

@PackageScope
@TupleConstructor(includeFields = true, defaults = false)
@NullCheck(includeGenerated = true)
@EqualsAndHashCode
class ProviderModel<T> implements Model<T> {

    final String name
    private final Provider<T> modelProvider

    @Override
    T get() {
        this.modelProvider.get()
    }

    @Override
    String toString() {
        "ProviderModel($this.name)"
    }
}
