package com.jessebrault.ssg.buildscript.dsl

import com.jessebrault.ssg.buildscript.Build

final class AllBuildsDelegateTests extends AbstractBuildDelegateTests<Build.AllBuilds> {

    @Override
    protected AbstractBuildDelegate<Build.AllBuilds> getDelegate() {
        new AllBuildsDelegate()
    }

}
