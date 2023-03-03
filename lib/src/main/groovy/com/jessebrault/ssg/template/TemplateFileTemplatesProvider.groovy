package com.jessebrault.ssg.template

import com.jessebrault.ssg.provider.AbstractFileCollectionProvider
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import org.jetbrains.annotations.Nullable
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@NullCheck
@EqualsAndHashCode(includeFields = true)
class TemplateFileTemplatesProvider extends AbstractFileCollectionProvider<Template> implements TemplatesProvider {

    private static final Logger logger = LoggerFactory.getLogger(TemplateFileTemplatesProvider)

    private final Collection<TemplateType> templateTypes

    TemplateFileTemplatesProvider(File templatesDir, Collection<TemplateType> templateTypes) {
        super(templatesDir)
        this.templateTypes = Objects.requireNonNull(templateTypes)
    }

    private @Nullable TemplateType getType(String extension) {
        this.templateTypes.find {
            it.ids.contains(extension)
        }
    }

    @Override
    protected Template transformFileToT(File file, String relativePath, String extension) {
        def templateType = getType(extension)
        if (templateType == null) {
            logger.warn('there is no TemplateType for template {}, ignoring', relativePath)
        }
        templateType ? new Template(file.text, relativePath, templateType) : null
    }

    @Override
    Collection<TemplateType> getTemplateTypes() {
        this.templateTypes
    }

    @Override
    String toString() {
        "TemplateFileTemplatesProvider(templatesDir: ${ this.dir }, templateTypes: ${ this.templateTypes })"
    }

}
