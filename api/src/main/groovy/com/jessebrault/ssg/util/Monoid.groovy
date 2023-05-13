package com.jessebrault.ssg.util

import org.jetbrains.annotations.ApiStatus

@ApiStatus.Experimental
interface Monoid<T> extends Semigroup<T>, Zero<T> {}
