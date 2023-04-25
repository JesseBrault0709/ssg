package com.jessebrault.ssg.task

import com.jessebrault.ssg.util.Diagnostic

interface Task {
    String getName()
    Collection<Diagnostic> execute(Collection<Task> allTasks)
}