package com.jessebrault.ssg.url

interface UrlBuilder {
    String getAbsolute()
    String relative(String to)
}