package com.jessebrault.ssg.provider

abstract class AbstractCollectionProvider<T> implements CollectionProvider<T> {

    static <T> CollectionProvider<T> concat(
            CollectionProvider<T> cp0,
            CollectionProvider<T> cp1
    ) {
        ClosureBasedCollectionProvider.get {
            cp0.provide() + cp1.provide()
        }
    }

    @Override
    CollectionProvider<T> plus(Provider<T> other) {
        concat(this, other as CollectionProvider<T>)
    }

    @Override
    CollectionProvider<T> plus(CollectionProvider<T> other) {
        concat(this, other)
    }

}
