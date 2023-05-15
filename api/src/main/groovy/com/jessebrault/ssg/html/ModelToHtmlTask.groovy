package com.jessebrault.ssg.html

import com.jessebrault.ssg.SiteSpec
import com.jessebrault.ssg.model.Model
import com.jessebrault.ssg.model.ModelInput
import com.jessebrault.ssg.part.Part
import com.jessebrault.ssg.render.RenderContext
import com.jessebrault.ssg.task.Task
import com.jessebrault.ssg.task.TaskSpec
import com.jessebrault.ssg.template.Template
import com.jessebrault.ssg.text.Text
import com.jessebrault.ssg.util.Result
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck

@NullCheck
@EqualsAndHashCode
final class ModelToHtmlTask<T> extends AbstractHtmlTask<ModelInput<T>> {

    private final SiteSpec siteSpec
    private final Map<String, Object> globals
    private final Model<T> model
    private final Template template
    private final Collection<Text> allTexts
    private final Collection<Model<Object>> allModels
    private final Collection<Part> allParts

    ModelToHtmlTask(
            String relativeHtmlPath,
            TaskSpec taskSpec,
            Model<T> model,
            Template template,
            Collection<Text> allTexts,
            Collection<Model<Object>> allModels,
            Collection<Part> allParts
    ) {
        super(
                "modelToHtml:${ relativeHtmlPath }",
                relativeHtmlPath,
                new ModelInput<>(model.name, model),
                taskSpec.outputDir
        )
        this.siteSpec = taskSpec.siteSpec
        this.globals = taskSpec.globals
        this.model = model
        this.template = template
        this.allTexts = allTexts
        this.allModels = allModels
        this.allParts = allParts
    }

    @Override
    protected Result<String> transform(Collection<Task> allTasks) {
        this.template.type.renderer.render(this.template, null, new RenderContext(
                sourcePath: this.model.name,
                targetPath: this.htmlPath,
                tasks: allTasks,
                texts: this.allTexts,
                models: this.allModels,
                parts: this.allParts,
                siteSpec: this.siteSpec,
                globals: this.globals
        ))
    }

    @Override
    String toString() {
        "ModelToHtml(${ this.model }, ${ this.template }, ${ this.allTexts }, ${ this.allParts }, ${ super.toString() })"
    }

}
