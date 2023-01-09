package com.jessebrault.ssg.buildscript

import com.jessebrault.ssg.Build
import com.jessebrault.ssg.Config

interface BuildScriptRunner {
    Collection<Build> runBuildScript(String relativePath, Config defaultConfig, Map defaultGlobals)
}