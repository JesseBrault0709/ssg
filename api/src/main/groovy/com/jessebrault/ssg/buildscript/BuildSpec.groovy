package com.jessebrault.ssg.buildscript

import com.jessebrault.ssg.model.Model
import com.jessebrault.ssg.text.TextConverter
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groowt.util.di.RegistryObjectFactory
import groowt.util.fp.provider.Provider

import static com.jessebrault.ssg.util.ObjectUtil.requireProvider
import static com.jessebrault.ssg.util.ObjectUtil.requireString

@NullCheck(includeGenerated = true)
@EqualsAndHashCode
final class BuildSpec {

    final String name
    final Provider<Set<String>> basePackages
    final Provider<String> siteName
    final Provider<String> baseUrl
    final Provider<File> outputDir
    final Provider<Map<String, Object>> globals
    final Provider<Set<Model>> models
    final Provider<Set<File>> textsDirs
    final Provider<Set<TextConverter>> textConverters
    final Provider<RegistryObjectFactory.Builder> objectFactoryBuilder

    @SuppressWarnings('GroovyAssignabilityCheck')
    BuildSpec(Map args) {
        this.name = requireString(args.name)
        this.basePackages = requireProvider(args.basePackages)
        this.siteName = requireProvider(args.siteName)
        this.baseUrl = requireProvider(args.baseUrl)
        this.outputDir = requireProvider(args.outputDir)
        this.globals = requireProvider(args.globals)
        this.models = requireProvider(args.models)
        this.textsDirs = requireProvider(args.textsDirs)
        this.textConverters = requireProvider(args.textConverters)
        this.objectFactoryBuilder = requireProvider(args.objectFactoryBuilder)
    }

    @Override
    String toString() {
        "Build(name: ${this.name}, basePackages: $basePackages, siteName: $siteName, " +
                "baseUrl: $baseUrl, outputDir: $outputDir, textsDirs: $textsDirs)"
    }

}
