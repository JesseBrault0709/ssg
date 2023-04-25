package com.jessebrault.ssg.template

import static org.mockito.Mockito.mock

final class TemplateMocks {

    static Template blankTemplate() {
        def templateRenderer = mock(TemplateRenderer)
        new Template('', new TemplateType([], templateRenderer), '')
    }

}
