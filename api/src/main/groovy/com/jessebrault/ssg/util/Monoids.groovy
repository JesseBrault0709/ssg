package com.jessebrault.ssg.util

import org.jetbrains.annotations.ApiStatus

import java.util.function.BinaryOperator

@ApiStatus.Experimental
final class Monoids {

    static <T> Monoid<T> of(T zero, BinaryOperator<T> concat) {
        new SimpleMonoid<>(zero, concat)
    }

    static <T> Monoid<Collection<T>> getMergeCollectionMonoid(Eq<T> tEq, Semigroup<T> tSemigroup) {
        new SimpleMonoid<>([], { Collection<T> c0, Collection<T> c1 ->
            def r = [] as Collection<T>
            r.addAll(c0)
            c1.each { T t ->
                def sameT = r.find { tEq.areEqual.test(it, t) }
                if (sameT != null) {
                    r.remove(sameT)
                    r << tSemigroup.concat.apply(sameT, t)
                } else {
                    r << t
                }
            }
            r
        })
    }

    private Monoids() {}

}
