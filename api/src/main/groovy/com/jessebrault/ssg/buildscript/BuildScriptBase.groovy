package com.jessebrault.ssg.buildscript


import com.jessebrault.ssg.buildscript.Build.AllBuilds
import com.jessebrault.ssg.buildscript.dsl.AllBuildsDelegate
import com.jessebrault.ssg.buildscript.dsl.BuildDelegate

abstract class BuildScriptBase extends Script {

    private final Collection<AllBuildsDelegate> allBuildsDelegates = []
    private final Collection<BuildDelegate> buildDelegates = []

    private int currentBuildNumber = 0

    final AllBuilds defaultAllBuilds = AllBuilds.getEmpty()

    void build(
            @DelegatesTo(value = BuildDelegate, strategy = Closure.DELEGATE_FIRST)
            Closure<Void> buildClosure
    ) {
        this.build('build' + this.currentBuildNumber, buildClosure)
    }

    void build(
            String name,
            @DelegatesTo(value = BuildDelegate, strategy = Closure.DELEGATE_FIRST)
            Closure<Void> buildClosure
    ) {
        def d = new BuildDelegate().tap {
            it.name = name
        }
        buildClosure.setDelegate(d)
        buildClosure.setResolveStrategy(Closure.DELEGATE_FIRST)
        buildClosure()
        this.buildDelegates << d
        this.currentBuildNumber++
    }

    void allBuilds(
            @DelegatesTo(value = AllBuildsDelegate, strategy = Closure.DELEGATE_FIRST)
            Closure<Void> allBuildsClosure
    ) {
        def d = new AllBuildsDelegate()
        allBuildsClosure.setDelegate(d)
        allBuildsClosure.setResolveStrategy(Closure.DELEGATE_FIRST)
        allBuildsClosure()
        this.allBuildsDelegates << d
    }

    Collection<Build> getBuilds() {
        def allBuilds = this.defaultAllBuilds
        this.allBuildsDelegates.each {
            allBuilds += it.getResult()
        }

        def baseBuild = Build.from(allBuilds)
        this.buildDelegates.collect {
            baseBuild + it.getResult()
        }
    }

}
