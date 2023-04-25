package com.jessebrault.ssg.template

import com.jessebrault.ssg.render.RenderContext
import com.jessebrault.ssg.util.Result
import com.jessebrault.ssg.text.Text

interface TemplateRenderer {

    Result<String> render(
            Template template,
            Text text,
            RenderContext context
    )

}
