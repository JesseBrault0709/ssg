package com.jessebrault.ssg.model

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.PackageScope
import groovy.transform.TupleConstructor

@PackageScope
@TupleConstructor(includeFields = true, defaults = false)
@NullCheck(includeGenerated = true)
@EqualsAndHashCode(includeFields = true)
final class ClosureBasedModel<T> implements Model<T> {

    final String name
    private final Closure<T> tClosure

    @Override
    T get() {
        this.tClosure()
    }

}
