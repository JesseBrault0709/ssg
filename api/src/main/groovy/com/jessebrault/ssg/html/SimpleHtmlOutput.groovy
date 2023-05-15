package com.jessebrault.ssg.html

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.PackageScope
import groovy.transform.TupleConstructor

@TupleConstructor(defaults = false)
@NullCheck(includeGenerated = true)
@EqualsAndHashCode
final class SimpleHtmlOutput implements HtmlOutput {
    final String name
    final File file
    final String htmlPath
}
