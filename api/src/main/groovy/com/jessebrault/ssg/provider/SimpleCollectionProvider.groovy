package com.jessebrault.ssg.provider

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.PackageScope
import groovy.transform.TupleConstructor

@PackageScope
@TupleConstructor(defaults = false, includeFields = true)
@NullCheck(includeGenerated = true)
@EqualsAndHashCode(includeFields = true)
final class SimpleCollectionProvider<T> extends AbstractCollectionProvider<T> {

    private final Collection<T> ts

    @Override
    Collection<T> provide() {
        this.ts
    }

}
