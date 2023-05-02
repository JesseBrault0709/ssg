package com.jessebrault.ssg.provider

interface Provider<T> {

    T provide()

    @Deprecated
    CollectionProvider<T> plus(Provider<T> other)

    CollectionProvider<T> asType(Class<CollectionProvider> collectionProviderClass)

    boolean isEmpty()

    default boolean isPresent() {
        !this.empty
    }

}