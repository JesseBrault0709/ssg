package com.jessebrault.ssg.text

import com.jessebrault.ssg.provider.AbstractFileCollectionProvider
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import org.jetbrains.annotations.Nullable
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@NullCheck
@EqualsAndHashCode(includeFields = true)
class TextFileTextsProvider extends AbstractFileCollectionProvider<Text> implements TextsProvider {

    private static final Logger logger = LoggerFactory.getLogger(TextFileTextsProvider)

    private final Collection<TextType> textTypes

    TextFileTextsProvider(File textsDir, Collection<TextType> textTypes) {
        super(textsDir)
        this.textTypes = Objects.requireNonNull(textTypes)
    }

    private TextType getTextType(String extension) {
        this.textTypes.find {
            it.ids.contains(extension)
        }
    }

    @Override
    protected @Nullable Text transformFileToT(File file, String relativePath, String extension) {
        def textType = getTextType(extension)
        if (!textType) {
            logger.warn('no TextType for text {}, ignoring', file.path)
        }
        textType ? new Text(file.text, relativePath, textType) : null
    }

    @Override
    Collection<TextType> getTextTypes() {
        this.textTypes
    }

    @Override
    String toString() {
        "TextFileTextsProvider(textsDir: ${ this.dir }, textTypes: ${ this.textTypes })"
    }

}
