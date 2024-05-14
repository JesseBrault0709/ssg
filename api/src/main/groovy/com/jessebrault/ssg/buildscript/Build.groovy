package com.jessebrault.ssg.buildscript

import com.jessebrault.ssg.model.Model
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groowt.util.fp.property.Property
import groowt.util.fp.provider.NamedProvider
import groowt.util.fp.provider.NamedSetProvider
import groowt.util.fp.provider.Provider
import groowt.util.fp.provider.SetProvider

import static java.util.Objects.requireNonNull

@NullCheck(includeGenerated = true)
@EqualsAndHashCode
final class Build {

    final Collection<String> includedBuilds
    final Property<String> name
    final Property<String> siteName
    final Property<String> baseUrl
    final Provider<File> outputDir
    final Provider<Map<String, Object>> globals
    final Set<Provider<File>> textsDirs
    final Set<NamedProvider<Model>> models

    @SuppressWarnings('GroovyAssignabilityCheck')
    Build(Map args) {
        this.includedBuilds = requireNonNull(args.includedBuilds)
        this.name = requireNonNull(args.name)
        this.siteName = requireNonNull(args.siteName)
        this.baseUrl = requireNonNull(args.baseUrl)
        this.outputDir = requireNonNull(args.outputDir)
        this.globals = requireNonNull(args.globals)
        this.textsDirs = requireNonNull(args.textsDirs)
        this.models = requireNonNull(args.models)
    }

    @Override
    String toString() {
        "Build(name: ${ this.name })"
    }

}
