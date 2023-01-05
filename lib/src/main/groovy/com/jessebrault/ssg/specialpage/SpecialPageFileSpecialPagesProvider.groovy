package com.jessebrault.ssg.specialpage

import com.jessebrault.ssg.util.FileNameHandler
import com.jessebrault.ssg.util.RelativePathHandler
import groovy.io.FileType
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@TupleConstructor(includeFields = true, defaults = false)
@NullCheck
class SpecialPageFileSpecialPagesProvider implements SpecialPagesProvider {

    private static final Logger logger = LoggerFactory.getLogger(SpecialPageFileSpecialPagesProvider)

    private final Collection<SpecialPageType> specialPageTypes
    private final File specialPagesDir

    private SpecialPageType getSpecialPageType(File file) {
        this.specialPageTypes.find {
            it.ids.contains(new FileNameHandler(file).getExtension())
        }
    }

    @Override
    Collection<SpecialPage> getSpecialPages() {
        if (!this.specialPagesDir.isDirectory()) {
            throw new IllegalArgumentException('specialPagesDir must be a directory')
        }

        def specialPages = []
        this.specialPagesDir.eachFileRecurse(FileType.FILES) {
            def type = this.getSpecialPageType(it)
            if (type != null) {
                def relativePath = this.specialPagesDir.relativePath(it)
                def path = new RelativePathHandler(relativePath).getWithoutExtension()
                logger.info('found specialPage {} with type {}', path, type)
                specialPages << new SpecialPage(it.text, path, type)
            } else {
                logger.warn('ignoring {} since there is no specialPageType for it', it)
            }
        }
        specialPages
    }

}
