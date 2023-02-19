package com.jessebrault.ssg.task

import com.jessebrault.ssg.Config
import com.jessebrault.ssg.SiteSpec
import groovy.transform.TupleConstructor
import groovy.transform.builder.Builder

@Builder
@TupleConstructor(defaults = false)
final class TaskContext {

    final Config config
    final SiteSpec siteSpec
    final Collection<OutputMeta> outputMetas

    @Override
    String toString() {
        "TaskContext(config: ${ this.config }, siteSpec: ${ this.siteSpec }, outputMetas: ${ this.outputMetas })"
    }

}
