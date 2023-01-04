package com.jessebrault.ssg

import groovy.transform.Canonical
import groovy.transform.MapConstructor

@Canonical
@MapConstructor
class SiteSpec {
    File buildDir
    File staticDir
    File textsDir
    File templatesDir
    File templatePartsDir
}
