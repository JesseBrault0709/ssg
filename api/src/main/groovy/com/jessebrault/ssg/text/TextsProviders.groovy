package com.jessebrault.ssg.text

import com.jessebrault.ssg.provider.CollectionProvider
import com.jessebrault.ssg.provider.CollectionProviders
import com.jessebrault.ssg.util.ExtensionUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.function.BiPredicate

final class TextsProviders {

    private static final Logger logger = LoggerFactory.getLogger(TextsProviders)

    static CollectionProvider<Text> from(File textsDir, Collection<TextType> types) {
        from(textsDir, types) { file, relativePath -> true }
    }

    static CollectionProvider<Text> from(
            File textsDir,
            Collection<TextType> textTypes,
            BiPredicate<File, String> filter
    ) {
        CollectionProviders.fromDirectory(textsDir) { file, relativePath ->
            if (filter.test(file, relativePath)) {
                def extension = ExtensionUtil.getExtension(relativePath)
                if (extension) {
                    def textType = textTypes.find { it.ids.contains(extension) }
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
            } else {
                return null
            }
        }
    }

    private TextsProviders() {}

}
