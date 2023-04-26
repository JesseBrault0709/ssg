package com.jessebrault.ssg.provider

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.PackageScope
import groovy.transform.TupleConstructor

import java.util.function.Supplier

@PackageScope
@TupleConstructor(defaults = false, includeFields = true)
@NullCheck(includeGenerated = true)
@EqualsAndHashCode(includeFields = true)
final class SupplierBasedCollectionProvider<T> extends AbstractCollectionProvider<T> {

    private final Supplier<Collection<T>> supplier

    @Override
    Collection<T> provide() {
        this.supplier.get()
    }

}
