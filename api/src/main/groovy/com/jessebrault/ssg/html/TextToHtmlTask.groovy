package com.jessebrault.ssg.html

import com.jessebrault.ssg.SiteSpec
import com.jessebrault.ssg.model.Model
import com.jessebrault.ssg.part.Part
import com.jessebrault.ssg.render.RenderContext
import com.jessebrault.ssg.task.Task
import com.jessebrault.ssg.task.TaskSpec
import com.jessebrault.ssg.template.Template
import com.jessebrault.ssg.text.Text
import com.jessebrault.ssg.text.TextInput
import com.jessebrault.ssg.util.Result
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck

@NullCheck
@EqualsAndHashCode(includeFields = true, callSuper = true)
final class TextToHtmlTask extends AbstractHtmlTask<TextInput> {

    private final SiteSpec siteSpec
    private final Map<String, Object> globals
    private final Text text
    private final Template template
    private final Collection<Text> allTexts
    private final Collection<Model<Object>> allModels
    private final Collection<Part> allParts

    TextToHtmlTask(
            String relativeHtmlPath,
            TaskSpec taskSpec,
            Text text,
            Template template,
            Collection<Text> allTexts,
            Collection<Model<Object>> allModels,
            Collection<Part> allParts
    ) {
        super(
                "textToHtml:${ relativeHtmlPath }",
                relativeHtmlPath,
                new TextInput(text.path, text),
                taskSpec.outputDir
        )
        this.siteSpec = taskSpec.siteSpec
        this.globals = taskSpec.globals
        this.text = text
        this.template = template
        this.allTexts = allTexts
        this.allModels = allModels
        this.allParts = allParts
    }

    @Override
    protected Result<String> transform(Collection<Task> allTasks) {
        this.template.type.renderer.render(this.template, this.text, new RenderContext(
                this.text.path,
                this.htmlPath,
                allTasks,
                this.allTexts,
                this.allModels,
                this.allParts,
                this.siteSpec,
                this.globals
        ))
    }

    @Override
    String toString() {
        "TextToHtml(${ this.text }, ${ this.template }, ${ this.allTexts }, ${ this.allParts }, ${ super.toString() })"
    }

}
