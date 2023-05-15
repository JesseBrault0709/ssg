package com.jessebrault.ssg.html

import com.jessebrault.ssg.task.FileOutput

interface HtmlOutput extends FileOutput {
    String getHtmlPath()
}