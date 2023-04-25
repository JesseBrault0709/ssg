package com.jessebrault.ssg.provider

interface CollectionProvider<T> {
    Collection<T> provide()
    CollectionProvider<T> plus(Provider<T> other)
    CollectionProvider<T> plus(CollectionProvider<T> other)
}