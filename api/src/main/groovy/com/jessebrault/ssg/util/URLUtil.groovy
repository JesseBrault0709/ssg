package com.jessebrault.ssg.util

final class URLUtil {

    static URL ofJarFile(File jarFile) {
       URI.create( "jar:file:$jarFile!/").toURL()
    }

}
