package com.jessebrault.ssg.specialpage

import com.jessebrault.ssg.part.Part
import com.jessebrault.ssg.text.Text

interface SpecialPageRenderer {

    String render(
            String text,
            Collection<Text> texts,
            Collection <Part> parts,
            Map globals
    )

}