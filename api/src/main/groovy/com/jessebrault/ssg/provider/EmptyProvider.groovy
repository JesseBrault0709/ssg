package com.jessebrault.ssg.provider

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.PackageScope

@PackageScope
@NullCheck
@EqualsAndHashCode
final class EmptyProvider<T> implements Provider<T> {

    @Override
    T provide() {
        throw new NullPointerException('EmptyProvider cannot provide a value.')
    }

    @Override
    CollectionProvider<T> plus(Provider<T> other) {
        CollectionProviders.fromSupplier {
            [other.provide()] as Collection<T>
        }
    }

    @Override
    CollectionProvider<T> asType(Class<CollectionProvider> collectionProviderClass) {
        CollectionProviders.fromCollection([]) as CollectionProvider<T>
    }

    @Override
    boolean isEmpty() {
        true
    }

    @Override
    String toString() {
        "EmptyProvider()"
    }

}
