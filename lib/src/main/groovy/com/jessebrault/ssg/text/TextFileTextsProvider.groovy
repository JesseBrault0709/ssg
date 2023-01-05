package com.jessebrault.ssg.text

import com.jessebrault.ssg.util.FileNameHandler
import com.jessebrault.ssg.util.RelativePathHandler
import groovy.io.FileType
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@TupleConstructor(includeFields = true, defaults = false)
@NullCheck
class TextFileTextsProvider implements TextsProvider {

    private static final Logger logger = LoggerFactory.getLogger(TextFileTextsProvider)

    private final Collection<TextType> textTypes
    private final File textsDir

    private TextType getTextType(File file) {
        this.textTypes.find {
            it.ids.contains(new FileNameHandler(file).getExtension())
        }
    }

    @Override
    Collection<Text> getTextFiles() {
        if (!this.textsDir.isDirectory()) {
            throw new IllegalArgumentException('textsDir must be a directory')
        }

        def textFiles = []
        this.textsDir.eachFileRecurse(FileType.FILES) {
            def type = this.getTextType(it)
            if (type != null) {
                def relativePath = this.textsDir.relativePath(it)
                def path = new RelativePathHandler(relativePath).getWithoutExtension()
                logger.debug('found textFile {} with type {}', path, type)
                textFiles << new Text(it.text, path, type)
            } else {
                logger.warn('ignoring {} because there is no textType for it', it)
            }
        }
        textFiles
    }

}
