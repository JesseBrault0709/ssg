package com.jessebrault.ssg.build

import com.jessebrault.ssg.model.Model
import com.jessebrault.ssg.page.Page
import groowt.util.di.RegistryObjectFactory
import groowt.util.fp.provider.NamedSetProvider

import static com.jessebrault.ssg.util.ObjectUtil.*

class Build {

    final String name
    final String siteName
    final String baseUrl
    final File outputDir
    final Map globals
    final Set<File> textsDirs
    final NamedSetProvider<Page> pages
    final RegistryObjectFactory objectFactory

    Build(Map args) {
        this.name = requireString(args.name)
        this.siteName = requireString(args.siteName)
        this.baseUrl = requireString(args.baseUrl)
        this.outputDir = requireFile(args.outputDir)
        this.globals = requireMap(args.globals)
        this.textsDirs = requireSet(args.textsDirs)
        this.pages = requireType(NamedSetProvider, args.pages)
        this.objectFactory = requireType(RegistryObjectFactory, args.objectFactory)
    }

    void doBuild() {
        // set up object factory for di
        // container should have: Build and all its properties
        // container should also have @Text, @Texts, @Page, and @Pages resolvers
    }

}
