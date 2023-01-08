package com.jessebrault.ssg.specialpage

import com.jessebrault.ssg.Diagnostic
import com.jessebrault.ssg.part.Part
import com.jessebrault.ssg.text.Text

interface SpecialPageRenderer {

    Tuple2<Collection<Diagnostic>, String> render(
            SpecialPage specialPage,
            Collection<Text> texts,
            Collection <Part> parts,
            Map globals
    )

}