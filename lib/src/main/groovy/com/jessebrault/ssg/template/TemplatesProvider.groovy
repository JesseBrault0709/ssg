package com.jessebrault.ssg.template

interface TemplatesProvider {
    Collection<Template> getTemplates()
    Collection<TemplateType> getTemplateTypes()
}
