package com.jessebrault.ssg

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@TupleConstructor(defaults = false)
@NullCheck(includeGenerated = true)
@EqualsAndHashCode
class Build {

    String name
    Config config
    SiteSpec siteSpec
    Map globals
    File outDir

    @Override
    String toString() {
        "Build(name: ${ this.name }, config: ${ this.config }, siteSpec: ${ this.siteSpec }, " +
                "globals: ${ this.globals }, outDir: ${ this.outDir })"
    }

}
