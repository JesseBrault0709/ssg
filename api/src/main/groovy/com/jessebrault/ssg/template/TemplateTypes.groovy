package com.jessebrault.ssg.template

import groovy.transform.NullCheck

@NullCheck
final class TemplateTypes {

    static TemplateType getGsp(Collection<String> extensions, ClassLoader parentClassLoader) {
        new TemplateType(extensions, new GspTemplateRenderer(parentClassLoader))
    }

    private TemplateTypes() {}

}
