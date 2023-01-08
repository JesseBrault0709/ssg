package com.jessebrault.ssg.template

import com.jessebrault.ssg.Diagnostic
import com.jessebrault.ssg.part.Part
import com.jessebrault.ssg.text.FrontMatter

interface TemplateRenderer {

    Tuple2<Collection<Diagnostic>, String> render(
            Template template,
            FrontMatter frontMatter,
            String text,
            Collection<Part> parts,
            Map globals
    )

}
