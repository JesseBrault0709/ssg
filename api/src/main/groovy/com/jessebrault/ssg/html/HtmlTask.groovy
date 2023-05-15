package com.jessebrault.ssg.html

import com.jessebrault.ssg.task.Task
import com.jessebrault.ssg.task.TaskInput

interface HtmlTask extends Task {
    @Deprecated
    String getHtmlPath()

    TaskInput getInput()
    HtmlOutput getOutput()
}