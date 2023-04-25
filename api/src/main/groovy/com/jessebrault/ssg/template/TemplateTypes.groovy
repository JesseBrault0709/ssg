package com.jessebrault.ssg.template

final class TemplateTypes {

    static final TemplateType GSP = new TemplateType(['.gsp'], new GspTemplateRenderer())

    private TemplateTypes() {}

}
