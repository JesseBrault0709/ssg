package com.jessebrault.ssg.task

import com.jessebrault.ssg.Build
import com.jessebrault.ssg.Result
import com.jessebrault.ssg.renderer.RenderContext
import com.jessebrault.ssg.util.ExtensionsUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.Marker
import org.slf4j.MarkerFactory

final class SpecialPageToHtmlFileTaskFactory implements TaskFactory<SpecialPageToHtmlFileTask> {

    private static final Logger logger = LoggerFactory.getLogger(SpecialPageToHtmlFileTaskFactory)
    private static final Marker enter = MarkerFactory.getMarker('ENTER')
    private static final Marker exit = MarkerFactory.getMarker('EXIT')

    @Override
    Result<TaskCollection<SpecialPageToHtmlFileTask>> getTasks(Build build) {
        logger.trace(enter, 'build: {}', build)
        logger.info('processing build with name {} for SpecialPageToHtmlFileTasks', build.name)

        def config = build.config
        def siteSpec = build.siteSpec
        def globals = build.globals

        def specialPages = config.specialPagesProviders.collectMany { it.provide() }
        def templates = config.templatesProviders.collectMany { it.provide() }
        def parts = config.partsProviders.collectMany { it.provide() }
        def texts = config.textProviders.collectMany { it.provide() }

        logger.debug('\n\tspecialPages: {}\n\ttemplates: {}\n\tparts: {}', specialPages, templates, parts)

        def tasks = new TaskCollection<SpecialPageToHtmlFileTask>(specialPages.findResults {
            logger.trace(enter, 'specialPage: {}', it)
            logger.info('processing specialPage with path: {}', it.path)

            def htmlPath = ExtensionsUtil.stripExtension(it.path) + '.html'

            def renderSpecialPage = { TaskContainer tasks, TaskTypeContainer taskTypes, Closure<Void> onDiagnostics ->
                def renderResult = it.type.renderer.render(
                        it,
                        new RenderContext(
                                config,
                                siteSpec,
                                globals,
                                texts,
                                parts,
                                it.path,
                                htmlPath,
                                tasks,
                                taskTypes
                        )
                )

                if (!renderResult.v1.isEmpty()) {
                    onDiagnostics(renderResult.v1)
                    ''
                } else {
                    renderResult.v2
                }
            }

            def result = new SpecialPageToHtmlFileTask(
                    "specialPageToHtmlFileTask:${ it.path }:${ htmlPath }",
                    it,
                    new HtmlFileOutput(
                            new File(build.outDir, htmlPath),
                            htmlPath,
                            renderSpecialPage
                    )
            )
            logger.trace(exit, 'result: {}', result)
            result
        })

        def result = new Result<>([], tasks)
        logger.trace(exit, 'result: {}', result)
        result
    }

}
