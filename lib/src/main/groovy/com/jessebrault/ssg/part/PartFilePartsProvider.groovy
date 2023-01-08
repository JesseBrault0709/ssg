package com.jessebrault.ssg.part

import com.jessebrault.ssg.util.FileNameHandler
import groovy.io.FileType
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@TupleConstructor(includeFields = true, defaults = false)
@NullCheck
@EqualsAndHashCode(includeFields = true)
class PartFilePartsProvider implements PartsProvider {

    private static final Logger logger = LoggerFactory.getLogger(PartFilePartsProvider)

    private final Collection<PartType> partTypes
    private final File partsDir

    private PartType getPartType(File file) {
        partTypes.find {
            it.ids.contains(new FileNameHandler(file).getExtension())
        }
    }

    @Override
    Collection<Part> getParts() {
        if (!partsDir.isDirectory()) {
            throw new IllegalArgumentException('partsDir must be a directory')
        }

        def parts = []
        partsDir.eachFileRecurse(FileType.FILES) {
            def type = this.getPartType(it)
            if (type != null) {
                def relativePath = this.partsDir.relativePath(it)
                logger.debug('found part {}', relativePath)
                parts << new Part(relativePath, type, it.text)
            } else {
                logger.warn('ignoring {} since there is no partType for it', it)
            }
        }
        parts
    }

    @Override
    Collection<PartType> getPartTypes() {
        this.partTypes
    }

    @Override
    String toString() {
        "PartFilePartsProvider(partsDir: ${ this.partsDir }, partTypes: ${ this.partTypes })"
    }

}
