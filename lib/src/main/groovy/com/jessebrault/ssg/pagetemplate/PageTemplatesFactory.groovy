package com.jessebrault.ssg.pagetemplate

interface PageTemplatesFactory {
    Collection<PageTemplate> getTemplates(File templatesDir)
}
