package com.jessebrault.ssg.template

import com.jessebrault.ssg.Diagnostic
import com.jessebrault.ssg.SiteSpec
import com.jessebrault.ssg.part.Part
import com.jessebrault.ssg.text.FrontMatter
import com.jessebrault.ssg.text.Text

interface TemplateRenderer {

    /**
     * TODO: get rid of frontMatter param since we can obtain it from the text
     */
    Tuple2<Collection<Diagnostic>, String> render(
            Template template,
            @Deprecated
            FrontMatter frontMatter,
            Text text,
            Collection<Part> parts,
            SiteSpec siteSpec,
            Map globals,
            String targetPath
    )

}
