package com.jessebrault.ssg.provider

interface Provider<T> {
    T provide()
    CollectionProvider<T> plus(Provider<T> other)
    CollectionProvider<T> asType(Class<CollectionProvider> collectionProviderClass)
}