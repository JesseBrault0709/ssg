package com.jessebrault.ssg.model

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.PackageScope
import groovy.transform.TupleConstructor

import java.util.function.Supplier

@PackageScope
@TupleConstructor(includeFields = true, defaults = false)
@NullCheck(includeGenerated = true)
@EqualsAndHashCode(includeFields = true)
final class SupplierBasedModel<T> implements Model<T> {

    final String name
    final Class<T> type

    private final Supplier<? extends T> supplier

    @Override
    T get() {
        this.supplier.get()
    }

    @Override
    String toString() {
        "SupplierBasedModel(name: $name, type: $type.name)"
    }
}
