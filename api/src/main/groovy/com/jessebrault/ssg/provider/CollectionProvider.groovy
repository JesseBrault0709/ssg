package com.jessebrault.ssg.provider

interface CollectionProvider<T> {
    Collection<T> provide()

    boolean contains(Provider<T> provider)
    boolean contains(CollectionProvider<T> collectionProvider)

    CollectionProvider<T> plus(Provider<T> other)
    CollectionProvider<T> plus(CollectionProvider<T> other)

    boolean isCase(Provider<T> provider)
    boolean isCase(CollectionProvider<T> collectionProvider)
}