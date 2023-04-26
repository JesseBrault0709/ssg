package com.jessebrault.ssg.model

import java.util.function.Supplier

final class Models {

    static <T> Model<T> of(String name, T t) {
        new SimpleModel<>(name, t)
    }

    static <T> Model<T> fromSupplier(String name, Supplier<T> tClosure) {
        new SupplierBasedModel<>(name, tClosure)
    }

    private Models() {}

}
