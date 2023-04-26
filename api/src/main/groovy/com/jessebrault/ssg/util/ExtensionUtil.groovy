package com.jessebrault.ssg.util

import org.jetbrains.annotations.Nullable

import java.util.regex.Pattern

final class ExtensionUtil {

    private static final Pattern stripExtensionPattern = ~/(.+)\..+$/
    private static final Pattern getExtensionPattern = ~/.+(\..+)$/

    static String stripExtension(String path) {
        def m = stripExtensionPattern.matcher(path)
        m.matches() ? m.group(1) : path
    }

    static @Nullable String getExtension(String path) {
        def m = getExtensionPattern.matcher(path)
        if (m.matches()) {
            m.group(1)
        } else {
            null
        }
    }

    private ExtensionUtil() {}

}
