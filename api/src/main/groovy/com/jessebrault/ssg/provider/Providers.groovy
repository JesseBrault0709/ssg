package com.jessebrault.ssg.provider

import groovy.transform.NullCheck

import java.util.function.Supplier

@NullCheck
final class Providers {

    static <T> Provider<T> getEmpty() {
        new EmptyProvider<>()
    }

    static <T> Provider<T> of(T t) {
        new SimpleProvider<>(t)
    }

    static <T> Provider<T> fromSupplier(Supplier<T> supplier) {
        new SupplierBasedProvider<>(supplier)
    }

    static <T> CollectionProvider<T> concat(Provider<T> ...providers) {
        concat(List.of(providers))
    }

    static <T> CollectionProvider<T> concat(Collection<Provider<T>> providers) {
        providers.inject(CollectionProviders.<T>getEmpty()) { acc, val ->
            acc + val
        }
    }

    private Providers() {}

}
