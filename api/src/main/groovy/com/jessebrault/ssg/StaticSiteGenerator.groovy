package com.jessebrault.ssg

import com.jessebrault.ssg.util.Diagnostic

interface StaticSiteGenerator {
    Collection<Diagnostic> doBuild(
            File projectDir,
            String buildName,
            String buildScriptFqn,
            Map<String, String> buildScriptCliArgs
    )
}
