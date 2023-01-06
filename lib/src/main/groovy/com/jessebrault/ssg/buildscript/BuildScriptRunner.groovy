package com.jessebrault.ssg.buildscript

import com.jessebrault.ssg.Config

interface BuildScriptRunner {
    void runBuildScript(Config config, Map globals)
}