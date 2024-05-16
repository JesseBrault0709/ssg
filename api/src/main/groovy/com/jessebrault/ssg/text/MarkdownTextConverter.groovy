package com.jessebrault.ssg.text

import com.jessebrault.ssg.util.ExtensionUtil
import com.jessebrault.ssg.util.PathUtil

class MarkdownTextConverter implements TextConverter {

    private static final Set<String> handledExtensions = ['.md']

    @Override
    Set<String> getHandledExtensions() {
        handledExtensions
    }

    @Override
    Text convert(File textsDir, File textFile) {
        new MarkdownText(
                ExtensionUtil.stripExtension(textFile.name),
                PathUtil.relative(textsDir, textFile).toString(),
                textFile
        )
    }

}
