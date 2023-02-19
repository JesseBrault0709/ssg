package com.jessebrault.ssg.testutil

import com.jessebrault.ssg.Config
import com.jessebrault.ssg.SiteSpec
import com.jessebrault.ssg.renderer.RenderContext

class RenderContextUtil {

    static RenderContext getRenderContext(Map args = null) {
        new RenderContext(
                args?.config as Config ?: new Config(),
                args?.siteSpec as SiteSpec ?: new SiteSpec('', ''),
                args?.globals as Map ?: [:],
                args?.texts as Collection ?: [],
                args?.parts as Collection ?: [],
                args?.sourcePath as String ?: '',
                args?.targetPath as String ?: ''
        )
    }

}
