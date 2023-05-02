package com.jessebrault.ssg.util

final class ResourceUtil {

    static void copyResourceToWriter(String name, Writer target) {
        ResourceUtil.getClassLoader().getResourceAsStream(name).withReader {
            it.transferTo(target)
        }
    }

    static void copyResourceToFile(String name, File target) {
        ResourceUtil.getClassLoader().getResourceAsStream(name).withReader { Reader reader ->
            target.withWriter { Writer writer ->
                reader.transferTo(writer)
            }
        }
    }

    static String loadResourceAsString(String name) {
        def sw = new StringWriter()
        copyResourceToWriter(name, sw)
        sw.toString()
    }

    private ResourceUtil() {}

}
