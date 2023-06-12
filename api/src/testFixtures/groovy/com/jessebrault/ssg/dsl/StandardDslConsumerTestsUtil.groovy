package com.jessebrault.ssg.dsl

import org.jetbrains.annotations.Nullable

final class StandardDslConsumerTestsUtil {

    static @Nullable String readResource(String name, ClassLoader classLoader) {
        def resource = classLoader.getResourceAsStream(name)
        if (resource != null) {
            def writer = new StringWriter()
            resource.withReader {
                it.transferTo(writer)
            }
            writer.toString()
        } else {
            null
        }
    }

    static URL writeGroovyClass(String name,  List<String> packageParts, String text) {
        def tmpDir = File.createTempDir("standardDslConsumerTestsUtil")
        def packageFile = new File(tmpDir, (packageParts.inject('') { acc, part ->
            acc + File.separator + part
        }) as String)
        def classFile = new File(packageFile, "${ name }.groovy")
        classFile.createParentDirectories()
        classFile.write(text)
        tmpDir.toURI().toURL()
    }

    private StandardDslConsumerTestsUtil() {}

}
