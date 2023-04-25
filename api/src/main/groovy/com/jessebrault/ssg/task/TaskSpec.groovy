package com.jessebrault.ssg.task

import com.jessebrault.ssg.SiteSpec
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@TupleConstructor(defaults = false)
@NullCheck(includeGenerated = true)
@EqualsAndHashCode
final class TaskSpec {

    static TaskSpec getEmpty() {
        new TaskSpec('', new File(''), SiteSpec.getBlank(), [:])
    }

    final String buildName
    final File outputDir
    final SiteSpec siteSpec
    final Map<String, Object> globals

}
