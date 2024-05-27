package com.jessebrault.ssg.view

import groowt.view.component.AbstractViewComponent
import groowt.view.component.ComponentTemplate
import groowt.view.component.compiler.ComponentTemplateCompileUnit
import groowt.view.component.compiler.source.ComponentTemplateSource
import groowt.view.component.web.BaseWebViewComponent

import java.util.function.Function

abstract class WvcPageView extends BaseWebViewComponent implements PageView, WithHtmlHelpers {

    String pageTitle
    String url

    WvcPageView(ComponentTemplate template) {
        super(template)
    }

    WvcPageView(Class<? extends ComponentTemplate> templateClass) {
        super(templateClass)
    }

    WvcPageView(
            Function<? super Class<? extends AbstractViewComponent>, ComponentTemplateCompileUnit> compileUnitFunction
    ) {
        super(compileUnitFunction)
    }

    WvcPageView(ComponentTemplateSource source) {
        super(source)
    }

    WvcPageView(Object source) {
        super(source)
    }

    @Override
    void renderTo(Writer out) throws IOException {
        def sw = new StringWriter()
        super.renderTo(sw)
        out.write(this.prettyFormat(sw.toString()))
    }

}
