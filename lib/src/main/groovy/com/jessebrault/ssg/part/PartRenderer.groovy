package com.jessebrault.ssg.part

import com.jessebrault.ssg.Diagnostic
import com.jessebrault.ssg.SiteSpec
import com.jessebrault.ssg.text.EmbeddableText
import org.jetbrains.annotations.Nullable

interface PartRenderer {

    Tuple2<Collection<Diagnostic>, String> render(
            Part part,
            Map binding,
            SiteSpec siteSpec,
            Map globals,
            @Nullable EmbeddableText text,
            Collection<Part> allParts,
            String path,
            String targetPath
    )

}
