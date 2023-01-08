package com.jessebrault.ssg.template

import com.jessebrault.ssg.util.FileNameHandler
import groovy.io.FileType
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@TupleConstructor(includeFields = true, defaults = false)
@NullCheck
class TemplateFileTemplatesProvider implements TemplatesProvider {

    private static final Logger logger = LoggerFactory.getLogger(TemplateFileTemplatesProvider)

    private final Collection<TemplateType> templateTypes
    private final File templatesDir

    private TemplateType getType(File file) {
        this.templateTypes.find {
            it.extensions.contains(new FileNameHandler(file).getExtension())
        }
    }

    @Override
    Collection<Template> getTemplates() {
        if (!this.templatesDir.isDirectory()) {
            throw new IllegalArgumentException('templatesDir must be a directory')
        }
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

    @Override
    Collection<TemplateType> getTemplateTypes() {
        this.templateTypes
    }

}
