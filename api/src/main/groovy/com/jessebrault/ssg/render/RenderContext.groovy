package com.jessebrault.ssg.render

import com.jessebrault.ssg.SiteSpec
import com.jessebrault.ssg.model.Model
import com.jessebrault.ssg.part.Part
import com.jessebrault.ssg.task.Task
import com.jessebrault.ssg.text.Text
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@TupleConstructor(defaults = false, force = true)
@NullCheck(includeGenerated = true)
@EqualsAndHashCode
final class RenderContext {

    RenderContext(Map<String, Object> args = [:]) {
        this(
                args.sourcePath as String ?: '',
                args.targetPath as String ?: '',
                args.tasks as Collection<Task> ?: [],
                args.texts as Collection<Text> ?: [],
                args.models as Collection<Model<Object>> ?: [],
                args.parts as Collection<Part> ?: [],
                args.siteSpec as SiteSpec ?: SiteSpec.getBlank(),
                args.globals as Map<String, Object> ?: [:]
        )
    }

    final String sourcePath
    final String targetPath
    final Collection<Task> tasks
    final Collection<Text> texts
    final Collection<Model<Object>> models
    final Collection<Part> parts
    final SiteSpec siteSpec
    final Map<String, Object> globals

}
