package com.jessebrault.ssg.text

import com.jessebrault.ssg.util.FileNameHandler
import groovy.transform.TupleConstructor
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@TupleConstructor(includeFields = true)
class TextFilesFactoryImpl implements TextFilesFactory {

    private static final Logger logger = LoggerFactory.getLogger(TextFilesFactoryImpl)

    private final Collection<TextFileType> textFileTypes

    private TextFileType getTextFileType(File file) {
        textFileTypes.find {
            it.extensions.contains(new FileNameHandler(file).getExtension())
        }
    }

    @Override
    Collection<TextFile> getTextFiles(File textsDir) {
        if (!textsDir.isDirectory()) {
            throw new IllegalArgumentException('textsDir must be a directory')
        }

        def textFiles = []
        textsDir.eachFileRecurse {
            if (it.isFile()) {
                def type = this.getTextFileType(it)
                if (type != null) {
                    def relativePath = textsDir.relativePath(it)
                    logger.debug('found textFile {}', relativePath)
                    textFiles << new TextFile(it, relativePath, type)
                }
            }
        }
        textFiles
    }

}
