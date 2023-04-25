package com.jessebrault.ssg.html

import com.jessebrault.ssg.task.Task

interface HtmlTask extends Task {
    String getPath()
}