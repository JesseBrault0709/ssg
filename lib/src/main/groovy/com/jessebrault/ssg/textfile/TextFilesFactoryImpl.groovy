package com.jessebrault.ssg.textfile

import com.jessebrault.ssg.textfile.TextFile.Type
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class TextFilesFactoryImpl implements TextFilesFactory {

    private static final Logger logger = LoggerFactory.getLogger(TextFilesFactoryImpl)

    private static Type getType(File file) {
        Type.values().find {
            file.name.endsWith(it.extension)
        }
    }

    @Override
    Collection<TextFile> getTextFiles(File textsDir) {
        if (!textsDir.isDirectory()) {
            throw new IllegalArgumentException('textsDir must be a directory')
        }

        def textFiles = []
        textsDir.eachFileRecurse {
            def type = getType(it)
            if (type != null) {
                def relativePath = textsDir.relativePath(it)
                logger.debug('found textFile {}', relativePath)
                textFiles << new TextFile(it, relativePath, type)
            }
        }
        textFiles
    }

}
