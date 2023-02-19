package com.jessebrault.ssg.template

import com.jessebrault.ssg.Diagnostic
import com.jessebrault.ssg.renderer.RenderContext
import com.jessebrault.ssg.text.Text

interface TemplateRenderer {

    Tuple2<Collection<Diagnostic>, String> render(
            Template template,
            Text text,
            RenderContext context
    )

}
