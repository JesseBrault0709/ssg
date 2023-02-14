package com.jessebrault.ssg.part

import com.jessebrault.ssg.provider.AbstractFileCollectionProvider
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import org.jetbrains.annotations.Nullable
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@NullCheck
@EqualsAndHashCode(includeFields = true)
class PartFilePartsProvider extends AbstractFileCollectionProvider<Part> implements PartsProvider {

    private static final Logger logger = LoggerFactory.getLogger(PartFilePartsProvider)

    private final Collection<PartType> partTypes

    PartFilePartsProvider(File partsDir, Collection<PartType> partTypes) {
        super(partsDir)
        this.partTypes = Objects.requireNonNull(partTypes)
    }

    private @Nullable PartType getPartType(String extension) {
        this.partTypes.find {
            it.ids.contains(extension)
        }
    }

    @Override
    protected @Nullable Part transformFileToT(File file, String relativePath, String extension) {
        def partType = getPartType(extension)
        if (!partType) {
            logger.warn('there is no PartType for {}, ignoring', relativePath)
        }
        partType ? new Part(relativePath, partType, file.text) : null
    }

    @Override
    Collection<PartType> getPartTypes() {
        this.partTypes
    }

    @Override
    String toString() {
        "PartFilePartsProvider(partsDir: ${ this.dir }, partTypes: ${ this.partTypes })"
    }

}
