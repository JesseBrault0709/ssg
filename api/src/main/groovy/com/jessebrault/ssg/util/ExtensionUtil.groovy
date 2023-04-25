package com.jessebrault.ssg.util

import java.util.regex.Pattern

final class ExtensionUtil {

    private static final Pattern stripExtensionPattern = ~/(.+)\..+$/
    private static final Pattern getExtensionPattern = ~/.+(\..+)$/

    static String stripExtension(String path) {
        def m = stripExtensionPattern.matcher(path)
        m.matches() ? m.group(1) : path
    }

    static String getExtension(String path) {
        def m = getExtensionPattern.matcher(path)
        if (m.matches()) {
            m.group(1)
        } else {
            throw new IllegalArgumentException("cannot get extension for path: ${ path }")
        }
    }

    private ExtensionUtil() {}

}
