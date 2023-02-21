package com.jessebrault.ssg

import com.jessebrault.ssg.task.SpecialPageToHtmlFileTaskFactory
import com.jessebrault.ssg.task.TaskContainer
import com.jessebrault.ssg.task.TaskTypeContainer
import com.jessebrault.ssg.task.TextToHtmlFileTaskFactory
import groovy.transform.NullCheck
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.Marker
import org.slf4j.MarkerFactory

@NullCheck
class SimpleStaticSiteGenerator implements StaticSiteGenerator {

    private static final Logger logger = LoggerFactory.getLogger(SimpleStaticSiteGenerator)
    private static final Marker enter = MarkerFactory.getMarker('ENTER')
    private static final Marker exit = MarkerFactory.getMarker('EXIT')

    private static final TextToHtmlFileTaskFactory textHtmlFactory = new TextToHtmlFileTaskFactory()
    private static final SpecialPageToHtmlFileTaskFactory specialPageHtmlFactory = new SpecialPageToHtmlFileTaskFactory()

    @Override
    TaskTypeContainer getTaskTypes() {
        new TaskTypeContainer([textHtmlFactory.taskType, specialPageHtmlFactory.taskType])
    }

    @Override
    Result<TaskContainer> generate(Build build) {
        logger.trace(enter, 'build: {}', build)
        logger.info('processing build with name: {}', build.name)

        def tasks = new TaskContainer()
        def diagnostics = []

        def textsResult = textHtmlFactory.getTasks(build)
        tasks.addAll(textsResult.get())
        diagnostics.addAll(textsResult.diagnostics)

        def specialPagesResult = specialPageHtmlFactory.getTasks(build)
        tasks.addAll(specialPagesResult.get())
        diagnostics.addAll(specialPagesResult.diagnostics)

        logger.trace(exit, '\n\tdiagnostics: {}\n\ttasks: {}', diagnostics, tasks)
        new Result<>(diagnostics, tasks)
    }

    @Override
    String toString() {
        "SimpleStaticSiteGenerator()"
    }

}
