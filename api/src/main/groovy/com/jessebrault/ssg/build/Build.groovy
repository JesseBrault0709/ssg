package com.jessebrault.ssg.build

import com.jessebrault.ssg.model.Model
import com.jessebrault.ssg.page.Page
import groowt.util.fp.provider.NamedSetProvider

import static com.jessebrault.ssg.util.ObjectUtil.*

class Build {

    final String name
    final String siteName
    final String baseUrl
    final File outputDir
    final Map globals
    final Set<File> textsDirs
    final NamedSetProvider<Model> models
    final NamedSetProvider<Page> pages

    Build(Map args) {
        this.name = requireString(args.name)
        this.siteName = requireString(args.siteName)
        this.baseUrl = requireString(args.baseUrl)
        this.outputDir = requireFile(args.outputDir)
        this.globals = requireMap(args.globals)
        this.textsDirs = requireSet(args.textsDirs)
        this.models = requireType(NamedSetProvider, args.models)
        this.pages = requireType(NamedSetProvider, args.pages)
    }

    void doBuild() {
        // set up object factory for di
        // container should have: Build and all its properties
        // container should also have @Text, @Texts, @Model, @Models, and @Page resolvers
    }

}
