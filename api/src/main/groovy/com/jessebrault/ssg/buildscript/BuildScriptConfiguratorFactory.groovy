package com.jessebrault.ssg.buildscript

import java.util.function.Consumer

interface BuildScriptConfiguratorFactory {
    Consumer<BuildScriptBase> get()
}