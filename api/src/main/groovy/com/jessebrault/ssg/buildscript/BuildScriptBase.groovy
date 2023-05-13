package com.jessebrault.ssg.buildscript

import com.jessebrault.ssg.buildscript.delegates.BuildDelegate
import groovy.transform.PackageScope
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.Marker
import org.slf4j.MarkerFactory

import static java.util.Objects.requireNonNull

abstract class BuildScriptBase extends Script {

    protected static final Logger logger = LoggerFactory.getLogger(BuildScriptBase)
    protected static final Marker enter = MarkerFactory.getMarker('ENTER')
    protected static final Marker exit = MarkerFactory.getMarker('EXIT')

    protected final Collection<BuildSpec> buildSpecs = []

    /**
     * args keys: name (required), extending (optional)
     *
     * @param args
     * @param buildClosure
     */
    void abstractBuild(
            Map<String, Object> args,
            @DelegatesTo(value = BuildDelegate, strategy = Closure.DELEGATE_FIRST)
            Closure<?> buildClosure
    ) {
        this.buildSpecs << new BuildSpec(
                requireNonNull(args.name as String),
                true,
                args.extending != null ? BuildExtension.get(args.extending as String) : BuildExtension.getEmpty(),
                buildClosure
        )
    }

    /**
     * args keys: name (required), extending (optional)
     *
     * @param args
     * @param buildClosure
     */
    void build(
            Map<String, Object> args,
            @DelegatesTo(value = BuildDelegate, strategy = Closure.DELEGATE_FIRST)
            Closure<?> buildClosure
    ) {
        this.buildSpecs << new BuildSpec(
                requireNonNull(args.name as String),
                false,
                args.extending != null ? BuildExtension.get(args.extending as String) : BuildExtension.getEmpty(),
                buildClosure
        )
    }

    @PackageScope
    Collection<BuildSpec> getBuildSpecs() {
        this.buildSpecs
    }

}
