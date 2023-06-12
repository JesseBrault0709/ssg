package com.jessebrault.ssg.part

final class PartTypes {

    static PartType getGsp(Collection<String> extensions, File tmpDir, GroovyScriptEngine engine) {
        new PartType(extensions, new GspPartRenderer(tmpDir, engine))
    }

    private PartTypes() {}

}
