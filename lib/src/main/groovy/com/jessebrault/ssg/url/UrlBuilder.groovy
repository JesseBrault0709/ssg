package com.jessebrault.ssg.url

interface UrlBuilder {
    String getAbsolute()
    String absolute(String to)
    String relative(String to)
}