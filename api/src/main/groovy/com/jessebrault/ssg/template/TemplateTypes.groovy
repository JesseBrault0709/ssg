package com.jessebrault.ssg.template

final class TemplateTypes {

    @Deprecated
    static final TemplateType GSP = new TemplateType(['.gsp'], new GspTemplateRenderer(TemplateTypes.classLoader, []))

    static TemplateType getGsp(
            Collection<String> extensions,
            ClassLoader classLoader,
            Collection<URL> scriptBaseUrls
    ) {
        new TemplateType(extensions, new GspTemplateRenderer(classLoader, scriptBaseUrls))
    }

    private TemplateTypes() {}

}
