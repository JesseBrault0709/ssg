package com.jessebrault.ssg.provider

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.PackageScope
import groovy.transform.TupleConstructor

@PackageScope
@TupleConstructor(defaults = false, includeFields = true)
@NullCheck(includeGenerated = true)
@EqualsAndHashCode(includeFields = true)
final class ClosureBasedCollectionProvider<T> extends AbstractCollectionProvider<T> {

    static <T> CollectionProvider<T> get(Closure<Collection<T>> closure) {
        new ClosureBasedCollectionProvider<>(closure)
    }

    private final Closure<Collection<T>> closure

    @Override
    Collection<T> provide() {
        this.closure()
    }

}
