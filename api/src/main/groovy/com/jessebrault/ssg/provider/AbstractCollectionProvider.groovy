package com.jessebrault.ssg.provider

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@TupleConstructor(defaults = false, includeFields = true)
@NullCheck(includeGenerated = true)
@EqualsAndHashCode(includeFields = true)
abstract class AbstractCollectionProvider<T> implements CollectionProvider<T> {

    static <T> CollectionProvider<T> concat(
            CollectionProvider<T> cp0,
            CollectionProvider<T> cp1
    ) {
        new SupplierBasedCollectionProvider<>([cp0, cp1], {
            cp0.provide() + cp1.provide()
        })
    }

    private final Collection<CollectionProvider<T>> collectionProviderChildren

    @Override
    boolean contains(CollectionProvider<T> collectionProvider) {
        collectionProvider in this
    }

    @Override
    <C extends CollectionProvider<T>> boolean containsType(Class<C> childCollectionProviderClass) {
        if (childCollectionProviderClass.isAssignableFrom(this.class)) {
            true
        } else {
            this.collectionProviderChildren.inject(false) { acc, childProvider ->
                acc || childCollectionProviderClass.isAssignableFrom(childProvider.class)
                        || childProvider.containsType(childCollectionProviderClass)
            }
        }
    }

    @Override
    <C extends CollectionProvider<T>> Collection<C> getChildrenOfType(Class<C> childCollectionProviderClass) {
        this.collectionProviderChildren.inject([] as Collection<C>) { acc, childProvider ->
            if (childCollectionProviderClass.isAssignableFrom(childProvider.class)) {
                acc + childProvider.getChildrenOfType(childCollectionProviderClass) + (childProvider as C)
            } else {
                acc + childProvider.getChildrenOfType(childCollectionProviderClass)
            }
        }
    }

    @Override
    Collection<DirectoryCollectionProvider<T>> getDirectoryCollectionProviderChildren() {
        this.getChildrenOfType(DirectoryCollectionProvider)
    }

    @Override
    CollectionProvider<T> plus(CollectionProvider<T> other) {
        concat(this, other)
    }

    @Override
    boolean isCase(CollectionProvider<T> collectionProvider) {
        collectionProvider == this
                || collectionProvider in this.collectionProviderChildren
                || this.collectionProviderChildren.inject(
                        false,
                        { acc, childCollectionProvider ->
                            acc || collectionProvider in childCollectionProvider
                        }
                )
    }

}
