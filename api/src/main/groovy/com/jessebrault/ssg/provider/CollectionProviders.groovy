package com.jessebrault.ssg.provider

import org.jetbrains.annotations.Nullable

import java.util.function.BiFunction
import java.util.function.Supplier

final class CollectionProviders {

    static <T> CollectionProvider<T> getEmpty() {
        new SimpleCollectionProvider<>([])
    }

    static <T> CollectionProvider<T> fromCollection(Collection<T> ts) {
        new SimpleCollectionProvider<T>(ts)
    }

    static <T> CollectionProvider<T> fromSupplier(Supplier<Collection<T>> supplier) {
        new SupplierBasedCollectionProvider<>(supplier)
    }

    static <T> DirectoryCollectionProvider<T> fromDirectory(
            File baseDirectory,
            BiFunction<File, String, @Nullable T> elementFunction
    ) {
        new FileBasedCollectionProvider<>(baseDirectory, elementFunction)
    }

    private CollectionProviders() {}

}
