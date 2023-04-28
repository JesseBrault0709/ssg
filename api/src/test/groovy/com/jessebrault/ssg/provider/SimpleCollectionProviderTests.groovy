package com.jessebrault.ssg.provider

final class SimpleCollectionProviderTests extends AbstractCollectionProviderTests {

    @Override
    protected CollectionProvider<Integer> getCollectionProvider(Collection<Integer> ts) {
        new SimpleCollectionProvider<>(ts)
    }

}
