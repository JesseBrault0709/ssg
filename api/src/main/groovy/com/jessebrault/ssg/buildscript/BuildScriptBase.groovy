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

    private static Collection<String> convertExtendingArg(Object arg) {
        arg instanceof Collection<String> ? arg as Collection<String>
                : arg instanceof String ? [arg] as Collection<String> : []
    }

    protected final Collection<BuildSpec> buildSpecs = []

    /**
     * args keys and values:
     * <ul>
     *     <li><code>name: String<code></li>
     *     <li><code>extending?: String | Collection&lt;String&gt;</code></li>
     * </ul>
     *
     * @param args
     * @param buildClosure
     */
    void abstractBuild(
            Map<String, Object> args,
            @DelegatesTo(value = BuildDelegate, strategy = Closure.DELEGATE_FIRST)
            Closure<?> buildClosure
    ) {
        final Collection<String> extending = convertExtendingArg(args.extending)
        this.buildSpecs << BuildSpec.get(
                name: requireNonNull(args.name as String),
                isAbstract:  true,
                extending:  extending,
                buildClosure: buildClosure
        )
    }

    /**
     * args keys and values:
     * <ul>
     *     <li><code>name: String<code></li>
     *     <li><code>extending?: String | Collection&lt;String&gt;</code></li>
     * </ul>
     *
     * @param args
     * @param buildClosure
     */
    void build(
            Map<String, Object> args,
            @DelegatesTo(value = BuildDelegate, strategy = Closure.DELEGATE_FIRST)
            Closure<?> buildClosure
    ) {
        final Collection<String> extending = convertExtendingArg(args.extending)
        this.buildSpecs << BuildSpec.get(
                name: requireNonNull(args.name as String),
                isAbstract:  false,
                extending:  extending,
                buildClosure: buildClosure
        )
    }

    @PackageScope
    Collection<BuildSpec> getBuildSpecs() {
        this.buildSpecs
    }

}
