package com.jessebrault.ssg.text

import com.jessebrault.ssg.provider.CollectionProvider
import com.jessebrault.ssg.provider.CollectionProviders
import com.jessebrault.ssg.util.ExtensionUtil
import com.jessebrault.ssg.util.PathUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory

final class TextsProviders {

    private static final Logger logger = LoggerFactory.getLogger(TextsProviders)

    static CollectionProvider<Text> from(File textsDir, Collection<TextType> types) {
        CollectionProviders.from(textsDir) {
            def extension = ExtensionUtil.getExtension(it.path)
            def textType = types.find { it.ids.contains(extension) }
            if (!textType) {
                logger.warn('there is no TextType for file {}; skipping', it)
                null
            } else {
                new Text(PathUtil.relative(textsDir.path, it.path), textType, it.text)
            }
        }
    }

    private TextsProviders() {}

}
