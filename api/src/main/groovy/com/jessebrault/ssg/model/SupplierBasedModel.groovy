package com.jessebrault.ssg.model

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.PackageScope
import groovy.transform.TupleConstructor

import java.util.function.Supplier

@PackageScope
@NullCheck(includeGenerated = true)
@EqualsAndHashCode(includeFields = true)
final class SupplierBasedModel<T> implements Model<T> {

    final String name
    private final Supplier<? extends T> supplier

    SupplierBasedModel(String name, Supplier<? extends T> supplier) {
        this.name = name
        this.supplier = supplier
    }

    @Override
    T get() {
        this.supplier.get()
    }

}
