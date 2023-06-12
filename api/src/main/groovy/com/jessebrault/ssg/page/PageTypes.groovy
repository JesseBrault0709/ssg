package com.jessebrault.ssg.page

final class PageTypes {

    static PageType getGsp(Collection<String> extensions, File tmpDir, GroovyScriptEngine engine) {
        new PageType(extensions, new GspPageRenderer(tmpDir, engine))
    }

    private PageTypes() {}

}
