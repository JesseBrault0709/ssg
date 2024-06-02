package com.jessebrault.ssg.view

import com.jessebrault.ssg.util.Diagnostic
import groovy.transform.TupleConstructor
import groowt.util.fp.either.Either
import groowt.view.component.ComponentTemplate
import groowt.view.component.ViewComponent
import groowt.view.component.compiler.ComponentTemplateClassFactory
import groowt.view.component.compiler.ComponentTemplateCompilerConfiguration
import groowt.view.component.compiler.source.ComponentTemplateSource
import groowt.view.component.web.compiler.DefaultWebViewComponentTemplateCompileUnit

@TupleConstructor
class WvcCompiler {

    final ComponentTemplateCompilerConfiguration compilerConfiguration
    final ComponentTemplateClassFactory templateClassFactory

    Either<Diagnostic, ComponentTemplate> compileTemplate(
            Class<? extends ViewComponent> componentClass,
            String resourceName
    ) {
        def templateUrl = componentClass.getResource(resourceName)
        if (templateUrl == null) {
            return Either.left(new Diagnostic(
                    "Could not find templateResource: $resourceName"
            ))
        }
        def source = ComponentTemplateSource.of(templateUrl)
        def compileUnit = new DefaultWebViewComponentTemplateCompileUnit(
                source.descriptiveName,
                componentClass,
                source,
                componentClass.packageName
        )
        def compileResult = compileUnit.compile(compilerConfiguration)
        def templateClass = templateClassFactory.getTemplateClass(compileResult)
        def componentTemplate = templateClass.getConstructor().newInstance()
        return Either.right(componentTemplate)
    }

}
