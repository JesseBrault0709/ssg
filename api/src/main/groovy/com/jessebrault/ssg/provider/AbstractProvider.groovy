package com.jessebrault.ssg.provider

abstract class AbstractProvider<T> implements Provider<T> {

    static <T> CollectionProvider<T> concat(
            Provider<T> p0,
            Provider<T> p1
    ) {
        new SupplierBasedCollectionProvider<>([], [p0, p1], {
            [p0.provide(), p1.provide()]
        })
    }

    static <T> CollectionProvider<T> concat(
            Provider<T> p0,
            CollectionProvider<T> p1
    ) {
        new SupplierBasedCollectionProvider<>([p1], [p0], {
            [p0.provide(), *p1.provide()]
        })
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
    CollectionProvider<T> asType(Class<CollectionProvider> collectionProviderClass) {
        new SupplierBasedCollectionProvider<>({
            [this.provide() as T]
        })
    }

}
