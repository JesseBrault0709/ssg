package com.jessebrault.ssg.task

import java.util.regex.Pattern

final class TaskTypeContainer {

    private static final Pattern taskTypeNamePattern = ~/(.*)Task/

    @Delegate
    private final Set<Class<? extends Task>> taskTypes = []

    TaskTypeContainer(Collection<Class<? extends Task>> taskTypes = null) {
        if (taskTypes) {
            this.taskTypes.addAll(taskTypes)
        }
    }

    @Override
    Class<? extends Task> getProperty(String propertyName) {
        def result = this.taskTypes.find {
            def m = taskTypeNamePattern.matcher(it.simpleName)
            if (m.matches()) {
                def withoutTaskEnd = m.group(1)
                def uncapitalized = withoutTaskEnd.uncapitalize()
                return propertyName == uncapitalized
            } else {
                throw new IllegalStateException("invalid task type name: ${ it.simpleName }")
            }
        }
        if (!result) {
            throw new IllegalStateException("no such taskType: ${ propertyName }")
        }
        result
    }

}
