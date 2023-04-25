package com.jessebrault.ssg.util

import java.nio.file.Path

final class PathUtil {

    static String relative(String base, String target) {
        Path.of(base).relativize(Path.of(target)).toString()
    }

    private PathUtil() {}

}
