package com.jessebrault.ssg.task

interface Task {
    Output getOutput()
    OutputMeta getOutputMeta()
}