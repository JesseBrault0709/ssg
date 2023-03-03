package com.jessebrault.ssg.task

interface FileOutput extends Output {
    File getFile()
    String getContent()
}
