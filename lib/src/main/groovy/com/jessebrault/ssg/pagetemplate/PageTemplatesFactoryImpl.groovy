package com.jessebrault.ssg.pagetemplate

import com.jessebrault.ssg.util.FileNameHandler
import groovy.transform.TupleConstructor
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@TupleConstructor(includeFields = true)
class PageTemplatesFactoryImpl implements PageTemplatesFactory {

    private static final Logger logger = LoggerFactory.getLogger(PageTemplatesFactoryImpl)

    private final Collection<PageTemplateType> types

    private PageTemplateType getType(File file) {
        this.types.find {
            it.extensions.contains(new FileNameHandler(file).getExtension())
        }
    }

    @Override
    Collection<PageTemplate> getTemplates(File templatesDir) {
        if (!templatesDir.isDirectory()) {
            throw new IllegalArgumentException('templatesDir must be a directory')
        }
        def templates = []
        templatesDir.eachFileRecurse {
            if (it.isFile()) {
                def type = this.getType(it)
                if (type != null) {
                    def relativePath = templatesDir.relativePath(it)
                    logger.debug('found template {}', relativePath)
                    templates << new PageTemplate(it, relativePath, type)
                }
            }
        }
        templates
    }

}
