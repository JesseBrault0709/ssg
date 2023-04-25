package com.jessebrault.ssg.html

import com.jessebrault.ssg.SiteSpec
import com.jessebrault.ssg.model.Model
import com.jessebrault.ssg.page.Page
import com.jessebrault.ssg.part.Part
import com.jessebrault.ssg.render.RenderContext
import com.jessebrault.ssg.task.Task
import com.jessebrault.ssg.task.TaskSpec
import com.jessebrault.ssg.text.Text
import com.jessebrault.ssg.util.Result
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck

@NullCheck
@EqualsAndHashCode(includeFields = true, callSuper = true)
final class PageToHtmlTask extends AbstractHtmlTask {

    private final SiteSpec siteSpec
    private final Map<String, Object> globals
    private final Page page
    private final Collection<Text> allTexts
    private final Collection<Model<Object>> allModels
    private final Collection<Part> allParts

    PageToHtmlTask(
            String path,
            TaskSpec taskSpec,
            Page page,
            Collection<Text> allTexts,
            Collection<Model<Object>> allModels,
            Collection<Part> allParts
    ) {
        super("pageToHtml:${ path }", path, taskSpec.outputDir)
        this.siteSpec = taskSpec.siteSpec
        this.globals = taskSpec.globals
        this.page = page
        this.allTexts = allTexts
        this.allModels = allModels
        this.allParts = allParts
    }

    @Override
    protected Result<String> transform(Collection<Task> allTasks) {
        this.page.type.renderer.render(this.page, new RenderContext(
                this.page.path,
                this.path,
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
        "PageToHtml(${ this.page }, ${ this.allTexts }, ${ this.allParts }, ${ super.toString() })"
    }

}
