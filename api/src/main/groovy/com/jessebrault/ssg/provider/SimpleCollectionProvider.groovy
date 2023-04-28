package com.jessebrault.ssg.provider

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.PackageScope

@PackageScope
@NullCheck
@EqualsAndHashCode(includeFields = true, callSuper = true)
final class SimpleCollectionProvider<T> extends AbstractCollectionProvider<T> {

    private final Collection<T> ts

    SimpleCollectionProvider(Collection<T> ts) {
        super([], [])
        this.ts = ts
    }

    @Override
    Collection<T> provide() {
        this.ts
    }

}
