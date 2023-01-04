package com.jessebrault.ssg.pagetemplate

import groovy.transform.Canonical

@Canonical
class PageTemplate {
    File file
    String relativePath
    PageTemplateType type
}
