package com.jessebrault.ssg.template

import com.jessebrault.ssg.part.Part

interface TemplateRenderer {
    String render(Template template, String text, Collection<Part> parts)
}
