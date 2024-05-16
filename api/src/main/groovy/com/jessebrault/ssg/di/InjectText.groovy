package com.jessebrault.ssg.di

import jakarta.inject.Qualifier

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD])
@interface InjectText {

    /**
     * The name of the text, or the path of the text, starting with '/'
     */
    String value()

}
