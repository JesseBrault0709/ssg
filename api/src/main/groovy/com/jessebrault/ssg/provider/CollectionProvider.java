package com.jessebrault.ssg.provider;

import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;

public interface CollectionProvider<T> {
    Collection<T> provide();

    boolean contains(Provider<T> provider);
    boolean contains(CollectionProvider<T> collectionProvider);

    @ApiStatus.Experimental
    <C extends CollectionProvider<T>> boolean containsType(Class<C> childCollectionProviderClass);

    @ApiStatus.Experimental
    <C extends CollectionProvider<T>> Collection<C> getChildrenOfType(Class<C> childCollectionProviderClass);

    Collection<DirectoryCollectionProvider<T>> getDirectoryCollectionProviderChildren();

    CollectionProvider<T> plus(Provider<T> other);
    CollectionProvider<T> plus(CollectionProvider<T> other);

    boolean isCase(Provider<T> provider);
    boolean isCase(CollectionProvider<T> collectionProvider);
}