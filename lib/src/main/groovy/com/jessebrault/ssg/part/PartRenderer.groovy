package com.jessebrault.ssg.part

import com.jessebrault.ssg.Diagnostic
import com.jessebrault.ssg.renderer.RenderContext
import com.jessebrault.ssg.text.Text
import org.jetbrains.annotations.Nullable

interface PartRenderer {

    Tuple2<Collection<Diagnostic>, String> render(
            Part part,
            Map binding,
            RenderContext context,
            @Nullable Text text
    )

}
