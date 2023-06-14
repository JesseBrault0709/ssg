package com.jessebrault.ssg.part

import groovy.transform.NullCheck

@NullCheck
final class PartTypes {

    static PartType getGsp(Collection<String> extensions, ClassLoader parentClassLoader) {
        new PartType(extensions, new GspPartRenderer(parentClassLoader))
    }

    private PartTypes() {}

}
