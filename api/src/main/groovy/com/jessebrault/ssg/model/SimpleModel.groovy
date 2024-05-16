package com.jessebrault.ssg.model

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.PackageScope
import groovy.transform.TupleConstructor

@PackageScope
@TupleConstructor(includeFields = true, defaults = false)
@NullCheck(includeGenerated = true)
@EqualsAndHashCode(includeFields = true)
final class SimpleModel<T> implements Model<T> {

    final String name
    final Class<T> type
    private final T t

    @Override
    T get() {
        this.t
    }

    @Override
    String toString() {
        "SimpleModel(name: $name, type: $type.name)"
    }

}
