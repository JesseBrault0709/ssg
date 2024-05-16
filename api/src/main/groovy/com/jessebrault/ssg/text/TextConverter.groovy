package com.jessebrault.ssg.text

interface TextConverter {
    Set<String> getHandledExtensions()
    Text convert(File textsDir, File textFile)
}
