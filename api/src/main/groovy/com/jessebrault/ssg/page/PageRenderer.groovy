package com.jessebrault.ssg.page

import com.jessebrault.ssg.render.RenderContext
import com.jessebrault.ssg.util.Result

interface PageRenderer {

    Result<String> render(
            Page specialPage,
            RenderContext context
    )

}