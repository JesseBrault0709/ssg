package com.jessebrault.ssg.task

interface WithInput<I extends Input> {
    I getInput()
}