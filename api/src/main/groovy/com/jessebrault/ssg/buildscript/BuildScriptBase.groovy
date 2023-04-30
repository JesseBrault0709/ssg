package com.jessebrault.ssg.buildscript

import com.jessebrault.ssg.buildscript.Build.AllBuilds
import com.jessebrault.ssg.buildscript.dsl.AllBuildsDelegate
import com.jessebrault.ssg.buildscript.dsl.BuildDelegate
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.Marker
import org.slf4j.MarkerFactory

abstract class BuildScriptBase extends Script {

    private static final Logger logger = LoggerFactory.getLogger(BuildScriptBase)
    private static final Marker enter = MarkerFactory.getMarker('ENTER')
    private static final Marker exit = MarkerFactory.getMarker('EXIT')

    private final Collection<AllBuildsDelegate> allBuildsDelegates = []
    private final Collection<BuildDelegate> buildDelegates = []

    private int currentBuildNumber = 0

    final AllBuilds defaultAllBuilds = AllBuilds.getEmpty()

    void build(
            @DelegatesTo(value = BuildDelegate, strategy = Closure.DELEGATE_FIRST)
            Closure<?> buildClosure
    ) {
        logger.trace(enter, 'buildClosure: {}', buildClosure)
        this.build('build' + this.currentBuildNumber, buildClosure)
        logger.trace(exit, '')
    }

    void build(
            String name,
            @DelegatesTo(value = BuildDelegate, strategy = Closure.DELEGATE_FIRST)
            Closure<?> buildClosure
    ) {
        logger.trace(enter, 'name: {}, buildClosure: {}', name, buildClosure)
        def d = new BuildDelegate().tap {
            it.name = name
        }
        buildClosure.setDelegate(d)
        buildClosure.setResolveStrategy(Closure.DELEGATE_FIRST)
        buildClosure()
        this.buildDelegates << d
        this.currentBuildNumber++
        logger.trace(exit, '')
    }

    void allBuilds(
            @DelegatesTo(value = AllBuildsDelegate, strategy = Closure.DELEGATE_FIRST)
            Closure<?> allBuildsClosure
    ) {
        logger.trace(enter, 'allBuildsClosure: {}', allBuildsClosure)
        def d = new AllBuildsDelegate()
        allBuildsClosure.setDelegate(d)
        allBuildsClosure.setResolveStrategy(Closure.DELEGATE_FIRST)
        allBuildsClosure()
        this.allBuildsDelegates << d
        logger.trace(exit, '')
    }

    Collection<Build> getBuilds() {
        logger.trace(enter, '')
        def allBuilds = this.defaultAllBuilds
        this.allBuildsDelegates.each {
            allBuilds += it.getResult()
        }

        def baseBuild = Build.from(allBuilds)
        def result = this.buildDelegates.collect {
            baseBuild + it.getResult()
        }
        logger.trace(exit, 'result: {}', result)
        result
    }

}
