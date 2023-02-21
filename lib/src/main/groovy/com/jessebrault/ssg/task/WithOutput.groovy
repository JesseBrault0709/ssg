package com.jessebrault.ssg.task

interface WithOutput<O extends Output> {
    O getOutput()
}