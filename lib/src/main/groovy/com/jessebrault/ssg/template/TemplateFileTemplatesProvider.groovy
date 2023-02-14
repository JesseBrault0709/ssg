package com.jessebrault.ssg.template

import com.jessebrault.ssg.provider.WithWatchableDir
import groovy.io.FileType
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static com.jessebrault.ssg.util.ExtensionsUtil.getExtension

@NullCheck
@EqualsAndHashCode(includeFields = true)
class TemplateFileTemplatesProvider implements TemplatesProvider, WithWatchableDir {

    private static final Logger logger = LoggerFactory.getLogger(TemplateFileTemplatesProvider)

    private final Collection<TemplateType> templateTypes
    private final File templatesDir

    TemplateFileTemplatesProvider(Collection<TemplateType> templateTypes, File templatesDir) {
        this.templateTypes = templateTypes
        this.templatesDir = templatesDir
        this.watchableDir = this.templatesDir
    }

    private TemplateType getType(File file) {
        def path = file.path
        this.templateTypes.find {
            it.ids.contains(getExtension(path))
        }
    }

    @Override
    Collection<Template> provide() {
        if (!this.templatesDir.isDirectory()) {
            logger.warn('templatesDir {} does not exist or is not a directory; skipping and providing no Templates', this.templatesDir)
            []
        } else {
            def templates = []
            this.templatesDir.eachFileRecurse(FileType.FILES) {
                def type = this.getType(it)
                if (type != null) {
                    def relativePath = this.templatesDir.relativePath(it)
                    logger.debug('found template {}', relativePath)
                    templates << new Template(it.text, relativePath, type)
                } else {
                    logger.warn('ignoring {} because there is no templateType for it', it)
                }
            }
            templates
        }
    }

    @Override
    Collection<TemplateType> getTemplateTypes() {
        this.templateTypes
    }

    @Override
    String toString() {
        "TemplateFileTemplatesProvider(templatesDir: ${ this.templatesDir }, templateTypes: ${ this.templateTypes })"
    }

}
