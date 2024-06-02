package com.jessebrault.ssg.page

import com.jessebrault.ssg.di.SelfPageExtension
import com.jessebrault.ssg.util.Diagnostic
import com.jessebrault.ssg.view.PageView
import com.jessebrault.ssg.view.WvcCompiler
import com.jessebrault.ssg.view.WvcPageView
import groowt.util.di.RegistryObjectFactory
import groowt.util.fp.either.Either

class DefaultWvcPage extends AbstractPage implements Page {

    final Class<? extends WvcPageView> viewType
    final String templateResource
    final RegistryObjectFactory objectFactory
    final WvcCompiler wvcCompiler

    DefaultWvcPage(Map args) {
        super(args)
        viewType = args.viewType
        templateResource = args.templateResource ?: viewType.simpleName + 'Template.wvc'
        objectFactory = args.objectFactory
        wvcCompiler = args.wvcCompiler
    }

    @Override
    Either<Diagnostic, PageView> createView() {
        WvcPageView pageView
        try {
            objectFactory.registry.getExtension(SelfPageExtension).currentPage = this
            pageView = objectFactory.createInstance(viewType)
        } catch (Exception exception) {
            return Either.left(new Diagnostic(
                    "There was an exception while constructing $viewType.name for $name",
                    exception
            ))
        }

        if (pageView.componentTemplate == null) {
            def compileResult = wvcCompiler.compileTemplate(viewType, templateResource)
            if (compileResult.isRight()) {
                pageView.componentTemplate = compileResult.getRight()
            } else {
                return Either.left(compileResult.getLeft())
            }
        }

        return Either.right(pageView)
    }

    @Override
    int hashCode() {
        Objects.hash(name, path, fileExtension, viewType, templateResource, objectFactory, wvcCompiler)
    }

    @Override
    boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false
        } else if (obj instanceof DefaultWvcPage) {
            return viewType == obj.viewType
                    && templateResource == obj.templateResource
                    && objectFactory == obj.objectFactory
                    && wvcCompiler == obj.wvcCompiler
        } else {
            return false
        }
    }

    @Override
    String toString() {
        "DefaultPage(name: $name, path: $path, fileExtension: $fileExtension, " +
                "viewType: $viewType, templateResource: $templateResource)"
    }

}
