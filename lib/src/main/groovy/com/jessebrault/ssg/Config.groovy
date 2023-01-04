package com.jessebrault.ssg

import com.jessebrault.ssg.frontmatter.FrontMatterGetter
import com.jessebrault.ssg.renderer.Renderer
import com.jessebrault.ssg.template.TemplatesFactory
import com.jessebrault.ssg.textfile.TextFilesFactory
import com.jessebrault.ssg.textrenderer.TextRenderer
import groovy.transform.Canonical
import groovy.transform.MapConstructor

@Canonical
@MapConstructor
class Config {
    TextFilesFactory textFilesFactory
    TemplatesFactory templatesFactory
    FrontMatterGetter markdownFrontMatterGetter
    TextRenderer markdownRenderer
    Renderer gspRenderer
}
