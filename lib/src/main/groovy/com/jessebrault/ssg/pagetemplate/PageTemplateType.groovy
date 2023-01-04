package com.jessebrault.ssg.pagetemplate

import groovy.transform.Canonical

@Canonical
class PageTemplateType {
    Collection<String> extensions
    PageRenderer renderer
}
