package com.jessebrault.ssg.buildscript

import com.jessebrault.ssg.buildscript.delegates.BuildDelegate
import org.jetbrains.annotations.ApiStatus
import org.jetbrains.annotations.Nullable
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.Marker
import org.slf4j.MarkerFactory

import static java.util.Objects.requireNonNull

@SuppressWarnings('unused')
abstract class BuildScriptBase extends Script {

    static final Logger logger = LoggerFactory.getLogger(BuildScriptBase)
    static final Marker enter = MarkerFactory.getMarker('ENTER')
    static final Marker exit = MarkerFactory.getMarker('EXIT')

    private String extending
    private Closure buildClosure
    private File projectRoot

    File getProjectRoot() {
        requireNonNull(this.projectRoot)
    }

    void setProjectRoot(File projectRoot) {
        this.projectRoot = requireNonNull(projectRoot)
    }

    File file(String name) {
        new File(this.projectRoot, name)
    }

    void build(@Nullable String extending, @DelegatesTo(value = BuildDelegate) Closure buildClosure) {
        this.extending = extending
        this.buildClosure = buildClosure
    }

    void build(@DelegatesTo(value = BuildDelegate) Closure buildClosure) {
        this.extending = null
        this.buildClosure = buildClosure
    }

    @ApiStatus.Internal
    @Nullable String getExtending() {
        this.extending
    }

    @ApiStatus.Internal
    Closure getBuildClosure() {
        this.buildClosure
    }

}
