package com.jessebrault.ssg.build

import jakarta.inject.Qualifier

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD])
@interface Page {

    /**
     * May be either a page name or a path starting with '/'
     */
    String value()

}
