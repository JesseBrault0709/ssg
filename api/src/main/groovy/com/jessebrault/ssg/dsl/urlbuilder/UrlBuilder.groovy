package com.jessebrault.ssg.dsl.urlbuilder

interface UrlBuilder {
    String getAbsolute()
    String absolute(String to)
    String relative(String to)
}