package com.jessebrault.ssg.provider

import org.codehaus.groovy.runtime.InvokerHelper

final class Providers {

    static <T> Provider<T> of(T t) {
        new SimpleProvider<>(t)
    }

    static <T> Provider<T> from(Closure<T> closure) {
        ClosureBasedProvider.of(closure)
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
