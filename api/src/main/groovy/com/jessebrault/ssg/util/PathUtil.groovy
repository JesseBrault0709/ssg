package com.jessebrault.ssg.util

import java.nio.file.Path

final class PathUtil {

    static File relative(File base, File target) {
        base.toPath().relativize(target.toPath()).toFile()
    }

    static String relative(String base, String target) {
        Path.of(base).relativize(Path.of(target)).toString()
    }

    static File resolve(File base, Path target) {
        base.toPath().resolve(target).toFile()
    }

    private PathUtil() {}

}
