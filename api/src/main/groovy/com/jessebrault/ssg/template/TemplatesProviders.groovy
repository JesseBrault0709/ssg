package com.jessebrault.ssg.template

import com.jessebrault.ssg.provider.CollectionProvider
import com.jessebrault.ssg.provider.CollectionProviders
import com.jessebrault.ssg.util.ExtensionUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory

final class TemplatesProviders {

    private static final Logger logger = LoggerFactory.getLogger(TemplatesProviders)

    static CollectionProvider<Template> from(File templatesDir, Collection<TemplateType> templateTypes) {
        CollectionProviders.fromDirectory(templatesDir) { file, relativePath ->
            def extension = ExtensionUtil.getExtension(relativePath)
            if (extension) {
                def templateType = templateTypes.find { it.ids.contains(extension) }
                if (!templateType) {
                    logger.debug('there is no TemplateType for file {}; skipping', file)
                    return null
                } else {
                    return new Template(relativePath, templateType, file.getText())
                }
            } else {
                logger.debug('there is no extension for file {}; skipping', file)
                return null
            }
        }
    }

    private TemplatesProviders() {}

}
