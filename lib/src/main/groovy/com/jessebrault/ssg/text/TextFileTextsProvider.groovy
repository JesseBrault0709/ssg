package com.jessebrault.ssg.text

import com.jessebrault.ssg.provider.WithWatchableDir
import com.jessebrault.ssg.util.FileNameHandler
import com.jessebrault.ssg.util.RelativePathHandler
import groovy.io.FileType
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@NullCheck
@EqualsAndHashCode(includeFields = true)
class TextFileTextsProvider implements TextsProvider, WithWatchableDir {

    private static final Logger logger = LoggerFactory.getLogger(TextFileTextsProvider)

    private final Collection<TextType> textTypes
    private final File textsDir

    TextFileTextsProvider(Collection<TextType> textTypes, File textsDir) {
        this.textTypes = textTypes
        this.textsDir = textsDir
        this.watchableDir = this.textsDir
    }

    private TextType getTextType(File file) {
        this.textTypes.find {
            it.ids.contains(new FileNameHandler(file).getExtension())
        }
    }

    @Override
    Collection<Text> provide() {
        if (!this.textsDir.isDirectory()) {
            logger.warn('textsDir {} does not exist or is not a directory; skipping and providing no Texts', this.textsDir)
            []
        } else {
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

    @Override
    Collection<TextType> getTextTypes() {
        this.textTypes
    }

    @Override
    String toString() {
        "TextFileTextsProvider(textsDir: ${ this.textsDir }, textTypes: ${ this.textTypes })"
    }

}
