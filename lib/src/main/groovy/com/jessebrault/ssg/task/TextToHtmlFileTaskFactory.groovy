package com.jessebrault.ssg.task

import com.jessebrault.ssg.Build
import com.jessebrault.ssg.Diagnostic
import com.jessebrault.ssg.Result
import com.jessebrault.ssg.renderer.RenderContext
import com.jessebrault.ssg.text.FrontMatter
import com.jessebrault.ssg.util.ExtensionsUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.Marker
import org.slf4j.MarkerFactory

final class TextToHtmlFileTaskFactory implements TaskFactory<TextToHtmlFileTask> {

    private static final Logger logger = LoggerFactory.getLogger(TextToHtmlFileTaskFactory)
    private static final Marker enter = MarkerFactory.getMarker('ENTER')
    private static final Marker exit = MarkerFactory.getMarker('EXIT')

    @Override
    TaskType<TextToHtmlFileTask> getTaskType() {
        TextToHtmlFileTask.TYPE
    }

    @Override
    Result<TaskCollection<TextToHtmlFileTask>> getTasks(Build build) {
        logger.trace(enter, 'build: {}', build)
        logger.info('getting TextToHtmlFileTasks for build with name: {}', build.name)

        def config = build.config
        def siteSpec = build.siteSpec
        def globals = build.globals
        def diagnostics = []

        // Get all texts, templates, parts, and specialPages
        def texts = config.textProviders.collectMany { it.provide() }
        def templates = config.templatesProviders.collectMany { it.provide() }
        def parts = config.partsProviders.collectMany { it.provide() }

        logger.debug('\n\ttexts: {}\n\ttemplates: {}\n\tparts: {}', texts, templates, parts)

        def tasks = new TaskCollection<TextToHtmlFileTask>(texts.findResults {
            logger.trace(enter, 'text: {}', it)
            logger.info('processing text with path: {}', it.path)

            def frontMatterResult = it.type.frontMatterGetter.get(it)
            FrontMatter frontMatter
            if (!frontMatterResult.v1.isEmpty()) {
                diagnostics.addAll(frontMatterResult.v1)
                logger.trace(exit, 'result: {}', null)
                return null
            } else {
                frontMatter = frontMatterResult.v2
                logger.debug('frontMatter: {}', frontMatter)
            }

            def desiredTemplate = frontMatter.find('template')
            if (desiredTemplate.isEmpty()) {
                logger.info('text with path {} has no \'template\' key in its frontMatter; skipping', it.path)
                logger.trace(exit, 'result: {}', null)
                return null
            }
            def template = templates.find { it.path == desiredTemplate.get() }
            if (template == null) {
                diagnostics << new Diagnostic("in text with path ${ it.path }, frontMatter.template refers to an unknown template: ${ desiredTemplate.get() }")
                logger.trace(exit, 'result: {}', null)
                return null
            }
            logger.debug('found template: {}', template)

            def htmlPath = ExtensionsUtil.stripExtension(it.path) + '.html'

            def renderTemplate = { TaskContainer tasks, TaskTypeContainer taskTypes, Closure<Void> onDiagnostics ->
                def templateRenderResult = template.type.renderer.render(
                        template,
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

                if (!templateRenderResult.v1.isEmpty()) {
                    onDiagnostics(templateRenderResult.v1)
                    ''
                } else {
                    templateRenderResult.v2
                }
            }

            def result = new TextToHtmlFileTask(
                    "textToHtmlFileTask:${ it.path }:${ htmlPath }",
                    it,
                    new HtmlFileOutput(
                            new File(build.outDir, htmlPath),
                            htmlPath,
                            renderTemplate
                    )
            )

            logger.trace(exit, 'result: {}', result)
            result
        })

        def result = new Result<>(diagnostics, tasks)
        logger.trace(exit, 'result: {}', result)
        result
    }

}
