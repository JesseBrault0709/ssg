package com.jessebrault.ssg.renderer

import com.jessebrault.ssg.Config
import com.jessebrault.ssg.SiteSpec
import com.jessebrault.ssg.part.Part
import com.jessebrault.ssg.task.TaskContainer
import com.jessebrault.ssg.task.TaskTypeContainer
import com.jessebrault.ssg.text.Text
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@TupleConstructor(defaults = false)
@NullCheck(includeGenerated = true)
@EqualsAndHashCode
final class RenderContext {
    final Config config
    final SiteSpec siteSpec
    final Map globals
    final Collection<Text> texts
    final Collection<Part> parts
    final String sourcePath
    final String targetPath
    final TaskContainer tasks
    final TaskTypeContainer taskTypes
}
