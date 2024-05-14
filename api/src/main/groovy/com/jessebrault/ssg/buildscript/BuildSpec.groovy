package com.jessebrault.ssg.buildscript

import com.jessebrault.ssg.model.Model
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groowt.util.fp.provider.NamedProvider
import groowt.util.fp.provider.Provider

import static com.jessebrault.ssg.util.ObjectUtil.*

@NullCheck(includeGenerated = true)
@EqualsAndHashCode
final class BuildSpec {

    final String name
    final Provider<String> siteName
    final Provider<String> baseUrl
    final Provider<File> outputDir
    final Provider<Map<String, Object>> globals
    final Set<Provider<File>> textsDirs

    @SuppressWarnings('GroovyAssignabilityCheck')
    BuildSpec(Map args) {
        this.name = requireString(args.name)
        this.siteName = requireProvider(args.siteName)
        this.baseUrl = requireProvider(args.baseUrl)
        this.outputDir = requireProvider(args.outputDir)
        this.globals = requireMap(args.globals)
        this.textsDirs = requireSet(args.textsDirs)
    }

    @Override
    String toString() {
        "Build(name: ${ this.name })"
    }

}
