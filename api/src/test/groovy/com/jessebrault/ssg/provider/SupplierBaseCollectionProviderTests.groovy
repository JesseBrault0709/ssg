package com.jessebrault.ssg.provider

final class SupplierBaseCollectionProviderTests extends AbstractCollectionProviderTests {

    @Override
    protected CollectionProvider<Integer> getCollectionProvider(Collection<Integer> ts) {
        new SupplierBasedCollectionProvider<Integer>({ ts })
    }

}
