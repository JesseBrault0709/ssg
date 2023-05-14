package com.jessebrault.ssg.part

import com.jessebrault.ssg.provider.CollectionProvider
import com.jessebrault.ssg.provider.CollectionProviders
import com.jessebrault.ssg.util.ExtensionUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory

final class PartsProviders {

    private static final Logger logger = LoggerFactory.getLogger(PartsProviders)

    static CollectionProvider<Part> from(File partsDir, Collection<PartType> partTypes) {
        CollectionProviders.fromDirectory(partsDir) { file, relativePath ->
            def extension = ExtensionUtil.getExtension(relativePath)
            if (extension) {
                def partType = partTypes.find { it.ids.contains(extension) }
                if (!partType) {
                    logger.debug('there is no PartType for file {}; skipping', file)
                    return null
                } else {
                    return new Part(relativePath, partType, file.getText())
                }
            } else {
                logger.debug('there is no extension for file {}; skipping', file)
                return null
            }
        }
    }

    private PartsProviders() {}

}
