package com.jessebrault.ssg.buildscript.dsl

import com.jessebrault.ssg.buildscript.Build

final class AllBuildsDelegate extends AbstractBuildDelegate<Build.AllBuilds> {

    @Override
    Build.AllBuilds getResult() {
        new Build.AllBuilds(
                this.getSiteSpecResult(),
                this.getGlobalsResult(),
                this.getTaskFactoriesResult(this.getSourcesResult(this.getTypesResult()))
        )
    }

}
