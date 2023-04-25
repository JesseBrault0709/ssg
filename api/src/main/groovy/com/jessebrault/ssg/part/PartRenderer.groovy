package com.jessebrault.ssg.part

import com.jessebrault.ssg.render.RenderContext
import com.jessebrault.ssg.util.Result
import com.jessebrault.ssg.text.Text
import org.jetbrains.annotations.Nullable

interface PartRenderer {

    Result<String> render(
            Part part,
            Map<String, Object> binding,
            RenderContext context,
            @Nullable Text text
    )

}
