package com.jessebrault.ssg.text

interface TextFilesFactory {
    Collection<TextFile> getTextFiles(File textsDir)
}
