package com.jessebrault.ssg.part

import com.jessebrault.ssg.provider.CollectionProvider
import com.jessebrault.ssg.provider.CollectionProviders
import com.jessebrault.ssg.util.ExtensionUtil
import com.jessebrault.ssg.util.PathUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory

final class PartsProviders {

    private static final Logger logger = LoggerFactory.getLogger(PartsProviders)

    static CollectionProvider<Part> of(File partsDir, Collection<PartType> partTypes) {
        CollectionProviders.from(partsDir) {
            def extension = ExtensionUtil.getExtension(it.path)
            def partType = partTypes.find { it.ids.contains(extension) }
            if (!partType) {
                logger.warn('there is no PartType for file {}; skipping', it)
                null
            } else {
                new Part(PathUtil.relative(partsDir.path, it.path), partType, it.getText())
            }
        }
    }

    private PartsProviders() {}

}
