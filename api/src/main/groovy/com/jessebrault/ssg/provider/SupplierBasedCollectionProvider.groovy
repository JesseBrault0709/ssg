package com.jessebrault.ssg.provider

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.PackageScope

import java.util.function.Supplier

@PackageScope
@NullCheck
@EqualsAndHashCode(includeFields = true, callSuper = true)
final class SupplierBasedCollectionProvider<T> extends AbstractCollectionProvider<T> {

    private final Supplier<Collection<T>> supplier

    SupplierBasedCollectionProvider(
            Collection<CollectionProvider<T>> collectionProviderChildren,
            Supplier<Collection<T>> supplier
    ) {
        super(collectionProviderChildren)
        this.supplier = supplier
    }

    SupplierBasedCollectionProvider(Supplier<Collection<T>> supplier) {
        this([], supplier)
    }

    @Override
    Collection<T> provide() {
        this.supplier.get()
    }

}
