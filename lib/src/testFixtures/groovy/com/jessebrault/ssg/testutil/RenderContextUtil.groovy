package com.jessebrault.ssg.testutil

import com.jessebrault.ssg.Config
import com.jessebrault.ssg.SiteSpec
import com.jessebrault.ssg.renderer.RenderContext
import com.jessebrault.ssg.task.Task
import com.jessebrault.ssg.task.TaskContainer
import com.jessebrault.ssg.task.TaskTypeContainer

class RenderContextUtil {

    static RenderContext getRenderContext(Map args = null) {
        new RenderContext(
                args?.config as Config ?: new Config(),
                args?.siteSpec as SiteSpec ?: new SiteSpec('', ''),
                args?.globals as Map ?: [:],
                args?.texts as Collection ?: [],
                args?.parts as Collection ?: [],
                args?.sourcePath as String ?: '',
                args?.targetPath as String ?: '',
                args?.tasks as TaskContainer ?: new TaskContainer(),
                args?.taskTypes as TaskTypeContainer ?: new TaskTypeContainer()
        )
    }

}
