package com.jessebrault.ssg.template

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class TemplatesFactoryImpl implements TemplatesFactory {

    private static final Logger logger = LoggerFactory.getLogger(TemplatesFactoryImpl)

    private static boolean hasTemplateExtension(File file) {
        file.name.endsWith('.gsp')
    }

    @Override
    Collection<Template> getTemplates(File templatesDir) {
        if (!templatesDir.isDirectory()) {
            throw new IllegalArgumentException('templatesDir must be a directory')
        }
        def templates = []
        templatesDir.eachFileRecurse {
            if (it.isFile() && hasTemplateExtension(it)) {
                def relativePath = templatesDir.relativePath(it)
                logger.debug('found template {}', relativePath)
                templates << new Template(it, relativePath)
            }
        }
        templates
    }

}
