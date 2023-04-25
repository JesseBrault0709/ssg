package com.jessebrault.ssg.dsl

interface DslScriptletProvider {
    String getGlobalsAvailable()
    String getRenderGlobal()

    String getLoggerAvailable()

    String getPartsAvailable()
    String getRenderPartFromParts()

    String getSiteSpecAvailable()
    String getRenderSiteSpecValues()

    String getSourcePathAvailable()
    String getRenderSourcePathValue()

    String getTagBuilderAvailable()

    String getTargetPathAvailable()
    String getRenderTargetPath()

    String getTextsAvailable()
    String getRenderTextFromTexts()

    String getUrlBuilderAvailable()
    String getUrlBuilderCorrectlyConfigured()
}