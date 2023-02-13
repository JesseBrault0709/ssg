package com.jessebrault.ssg.part

import com.jessebrault.ssg.Diagnostic
import com.jessebrault.ssg.text.EmbeddableText
import org.jetbrains.annotations.Nullable

interface PartRenderer {

    Tuple2<Collection<Diagnostic>, String> render(
            Part part,
            Map binding,
            Map globals,
            @Nullable EmbeddableText text,
            Collection<Part> allParts
    )

}
