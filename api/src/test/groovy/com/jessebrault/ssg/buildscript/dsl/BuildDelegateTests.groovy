package com.jessebrault.ssg.buildscript.dsl

import com.jessebrault.ssg.buildscript.Build

final class BuildDelegateTests extends AbstractBuildDelegateTests<Build> {

    @Override
    protected AbstractBuildDelegate<Build> getDelegate() {
        new BuildDelegate()
    }

}
