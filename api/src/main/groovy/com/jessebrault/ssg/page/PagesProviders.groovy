package com.jessebrault.ssg.page

import com.jessebrault.ssg.provider.CollectionProvider
import com.jessebrault.ssg.provider.CollectionProviders
import com.jessebrault.ssg.util.ExtensionUtil
import com.jessebrault.ssg.util.PathUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory

final class PagesProviders {

    private static final Logger logger = LoggerFactory.getLogger(PagesProviders)

    static CollectionProvider<Page> from(File pagesDirectory, Collection<PageType> pageTypes) {
        CollectionProviders.from(pagesDirectory) {
            def extension = ExtensionUtil.getExtension(it.path)
            def pageType = pageTypes.find { it.ids.contains(extension) }
            if (!pageType) {
                logger.warn('there is no PageType for file {}; skipping', it)
                null
            } else {
                new Page(PathUtil.relative(pagesDirectory.path, it.path), pageType, it.getText())
            }
        }
    }

    private PagesProviders() {}

}
