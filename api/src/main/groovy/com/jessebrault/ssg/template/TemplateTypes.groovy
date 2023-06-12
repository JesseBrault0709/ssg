package com.jessebrault.ssg.template

final class TemplateTypes {

    static TemplateType getGsp(Collection<String> extensions, File tmpDir, GroovyScriptEngine engine) {
        new TemplateType(extensions, new GspTemplateRenderer(tmpDir, engine))
    }

    private TemplateTypes() {}

}
