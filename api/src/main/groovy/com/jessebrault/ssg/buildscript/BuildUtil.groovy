package com.jessebrault.ssg.buildscript

import com.jessebrault.ssg.util.Diagnostic

final class BuildUtil {

    static Collection<Diagnostic> diagnoseIncludedBuilds(Collection<Build> allBuilds) {
        allBuilds.inject([] as Collection<Diagnostic>) { allDiagnostics, build ->
            allDiagnostics + build.includedBuilds.inject([] as Collection<Diagnostic>) { buildDiagnostics, includedBuildName ->
                def includedBuild = allBuilds.find { it.name == includedBuildName }
                if (includedBuild == null) {
                    buildDiagnostics + new Diagnostic(
                            "The includedBuild ${ includedBuildName } is not in the collection of allBuilds"
                    )
                } else {
                    buildDiagnostics
                }
            }
        }
    }

    private BuildUtil() {}

}
