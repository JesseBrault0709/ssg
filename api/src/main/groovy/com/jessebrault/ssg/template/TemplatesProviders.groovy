package com.jessebrault.ssg.template

import com.jessebrault.ssg.provider.CollectionProvider
import com.jessebrault.ssg.provider.CollectionProviders
import com.jessebrault.ssg.util.ExtensionUtil
import com.jessebrault.ssg.util.PathUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory

final class TemplatesProviders {

    private static final Logger logger = LoggerFactory.getLogger(TemplatesProviders)

    static CollectionProvider<Template> from(File templatesDir, Collection<TemplateType> templateTypes) {
        CollectionProviders.from(templatesDir) {
            def extension = ExtensionUtil.getExtension(it.path)
            def templateType = templateTypes.find { it.ids.contains(extension) }
            if (!templateType) {
                logger.warn('there is no TemplateType for file {}; skipping', it)
                null
            } else {
                new Template(PathUtil.relative(templatesDir.path, it.path), templateType, it.getText())
            }
        }
    }

    private TemplatesProviders() {}

}
