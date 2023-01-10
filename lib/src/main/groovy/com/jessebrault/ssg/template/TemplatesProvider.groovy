package com.jessebrault.ssg.template

import com.jessebrault.ssg.provider.Provider

interface TemplatesProvider extends Provider<Collection<Template>> {
    Collection<TemplateType> getTemplateTypes()
}
