package com.jessebrault.ssg.specialpage

import com.jessebrault.ssg.Diagnostic
import com.jessebrault.ssg.SiteSpec
import com.jessebrault.ssg.part.Part
import com.jessebrault.ssg.renderer.RenderContext
import com.jessebrault.ssg.text.Text

interface SpecialPageRenderer {

    Tuple2<Collection<Diagnostic>, String> render(
            SpecialPage specialPage,
            RenderContext context
    )

}