package com.jessebrault.ssg.part

final class PartTypes {

    @Deprecated
    static final PartType GSP = new PartType(['.gsp'], new GspPartRenderer(PartTypes.classLoader, []))

    static PartType getGsp(Collection<String> extensions, ClassLoader classLoader, Collection<URL> scriptBaseUrls) {
        new PartType(extensions, new GspPartRenderer(classLoader, scriptBaseUrls))
    }

    private PartTypes() {}

}
