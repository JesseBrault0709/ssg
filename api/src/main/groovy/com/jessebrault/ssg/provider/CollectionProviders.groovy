package com.jessebrault.ssg.provider

import groovy.transform.stc.ClosureParams
import groovy.transform.stc.FromString
import org.jetbrains.annotations.Nullable

final class CollectionProviders {

    static <T> CollectionProvider<T> getEmpty() {
        new SimpleCollectionProvider<>([])
    }

    static <T> CollectionProvider<T> of(Collection<T> ts) {
        new SimpleCollectionProvider<T>(ts)
    }

    static <T> CollectionProvider<T> from(Closure<Collection<T>> closure) {
        ClosureBasedCollectionProvider.get(closure)
    }

    static <T> CollectionProvider<T> from(
            File dir,
            @ClosureParams(value = FromString, options = 'java.io.File')
            Closure<@Nullable T> fileToElementClosure
    ) {
        new FileBasedCollectionProvider<>(dir, fileToElementClosure)
    }

    private CollectionProviders() {}

}
