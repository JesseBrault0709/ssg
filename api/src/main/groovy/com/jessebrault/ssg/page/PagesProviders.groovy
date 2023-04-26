package com.jessebrault.ssg.page

import com.jessebrault.ssg.provider.CollectionProvider
import com.jessebrault.ssg.provider.CollectionProviders
import com.jessebrault.ssg.util.ExtensionUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory

final class PagesProviders {

    private static final Logger logger = LoggerFactory.getLogger(PagesProviders)

    static CollectionProvider<Page> from(File pagesDirectory, Collection<PageType> pageTypes) {
        CollectionProviders.fromDirectory(pagesDirectory) { file, relativePath ->
            def extension = ExtensionUtil.getExtension(relativePath)
            if (extension) {
                def pageType = pageTypes.find { it.ids.contains(extension) }
                if (!pageType) {
                    logger.debug('there is no PageType for file {}; skipping', file)
                    return null
                } else {
                    return new Page(relativePath, pageType, file.getText())
                }
            } else {
                logger.debug('there is no extension for file {}; skipping', file)
                return null
            }
        }
    }

    private PagesProviders() {}

}
