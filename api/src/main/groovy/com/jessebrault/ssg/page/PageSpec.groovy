package com.jessebrault.ssg.page

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface PageSpec {
    String name()
    String path()
    String templateResource() default ''
    String fileExtension() default '.html'
}
