package com.jessebrault.ssg.provider

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@TupleConstructor(defaults = false, includeFields = true)
@NullCheck(includeGenerated = true)
@EqualsAndHashCode(includeFields = true)
abstract class AbstractCollectionProvider<T> implements CollectionProvider<T> {

    static <T> CollectionProvider<T> concat(
            CollectionProvider<T> cp,
            Provider<T> p
    ) {
        new SupplierBasedCollectionProvider<>([cp], [p], {
            [*cp.provide(), p.provide()]
        })
    }

    static <T> CollectionProvider<T> concat(
            CollectionProvider<T> cp0,
            CollectionProvider<T> cp1
    ) {
        new SupplierBasedCollectionProvider<>([cp0, cp1], [], {
            cp0.provide() + cp1.provide()
        })
    }

    private final Collection<CollectionProvider<T>> collectionProviderChildren
    private final Collection<Provider<T>> providerChildren

    @Override
    boolean contains(Provider<T> provider) {
        provider in this
    }

    @Override
    boolean contains(CollectionProvider<T> collectionProvider) {
        collectionProvider in this
    }

    @Override
    CollectionProvider<T> plus(Provider<T> other) {
        concat(this, other)
    }

    @Override
    CollectionProvider<T> plus(CollectionProvider<T> other) {
        concat(this, other)
    }

    @Override
    boolean isCase(Provider<T> provider) {
        provider in this.providerChildren || this.providerChildren.inject(false) { acc, childProvider ->
            acc || provider in childProvider
        }
    }

    @Override
    boolean isCase(CollectionProvider<T> collectionProvider) {
        collectionProvider == this
                || collectionProvider in this.collectionProviderChildren
                || this.collectionProviderChildren.inject(
                        false,
                        { acc, childCollectionProvider ->
                            acc || collectionProvider in childCollectionProvider
                        }
                )
    }

}
