package com.jessebrault.ssg.text

import com.jessebrault.ssg.provider.CollectionProvider
import com.jessebrault.ssg.provider.CollectionProviders
import com.jessebrault.ssg.util.ExtensionUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory

final class TextsProviders {

    private static final Logger logger = LoggerFactory.getLogger(TextsProviders)

    static CollectionProvider<Text> from(File textsDir, Collection<TextType> types) {
        CollectionProviders.fromDirectory(textsDir) { file, relativePath ->
            def extension = ExtensionUtil.getExtension(relativePath)
            if (extension) {
                def textType = types.find { it.ids.contains(extension) }
                if (!textType) {
                    logger.warn('there is no TextType for file {}; skipping', file)
                    return null
                } else {
                    return new Text(relativePath, textType, file.getText())
                }
            } else {
                logger.debug('there is no extension for file {}; skipping', file)
                return null
            }
        }
    }

    private TextsProviders() {}

}
