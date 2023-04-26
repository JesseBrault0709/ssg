package com.jessebrault.ssg.provider

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.PackageScope
import groovy.transform.TupleConstructor

import java.util.function.Supplier

@PackageScope
@TupleConstructor(includeFields = true, defaults = false)
@NullCheck(includeGenerated = true)
@EqualsAndHashCode(includeFields = true)
final class SupplierBasedProvider<T> extends AbstractProvider<T> {

    private final Supplier<T> supplier

    @Override
    T provide() {
        this.supplier.get()
    }

}
