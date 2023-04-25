package com.jessebrault.ssg.provider

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.PackageScope
import groovy.transform.TupleConstructor

@PackageScope
@TupleConstructor(includeFields = true, defaults = false)
@NullCheck(includeGenerated = true)
@EqualsAndHashCode(includeFields = true)
final class ClosureBasedProvider<T> extends AbstractProvider<T> {

    static <T> Provider<T> of(Closure<T> closure) {
        new ClosureBasedProvider<>(closure)
    }

    private final Closure<T> closure

    @Override
    T provide() {
        this.closure()
    }

}
