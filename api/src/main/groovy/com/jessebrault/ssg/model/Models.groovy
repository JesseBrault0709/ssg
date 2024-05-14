package com.jessebrault.ssg.model

import groowt.util.fp.provider.NamedProvider
import groowt.util.fp.provider.Provider

import java.util.function.Supplier

final class Models {

    static <T> Model<T> of(String name, T t) {
        new SimpleModel<>(name, t)
    }

    static <T> Model<T> ofSupplier(String name, Supplier<? extends T> tClosure) {
        new SupplierBasedModel<>(name, tClosure)
    }

    static <T> Model<T> ofProvider(String name, Provider<? extends T> modelProvider) {
        new ProviderModel<T>(name, modelProvider)
    }

    static <T> Model<T> ofNamedProvider(NamedProvider<? extends T> namedModelProvider) {
        new ProviderModel<T>(namedModelProvider.name, namedModelProvider)
    }

    private Models() {}

}
