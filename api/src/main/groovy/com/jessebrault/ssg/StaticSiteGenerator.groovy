package com.jessebrault.ssg

import com.jessebrault.ssg.util.Diagnostic

import java.util.function.Consumer

interface StaticSiteGenerator {
    boolean doBuild(String buildName, Consumer<Collection<Diagnostic>> diagnosticsConsumer)
}