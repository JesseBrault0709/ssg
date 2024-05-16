package com.jessebrault.ssg.model

import groowt.util.fp.provider.NamedProvider
import groowt.util.fp.provider.Provider

import java.util.function.Supplier

final class Models {

    @SuppressWarnings('GroovyAssignabilityCheck')
    static <T> Model<T> of(String name, T t) {
        new SimpleModel<>(name, t.class, t)
    }

    static <T> Model<T> ofSupplier(String name, Class<T> type, Supplier<? extends T> tClosure) {
        new SupplierBasedModel<>(name, type, tClosure)
    }

    static <T> Model<T> ofProvider(String name, Provider<? extends T> modelProvider) {
        new ProviderModel<T>(name, modelProvider.type, modelProvider)
    }

    static <T> Model<T> ofNamedProvider(NamedProvider<? extends T> namedModelProvider) {
        new ProviderModel<T>(namedModelProvider.name, namedModelProvider.type, namedModelProvider)
    }

    private Models() {}

}
