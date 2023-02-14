package com.jessebrault.ssg.specialpage

import com.jessebrault.ssg.provider.WithWatchableDir
import groovy.io.FileType
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static com.jessebrault.ssg.util.ExtensionsUtil.getExtension
import static com.jessebrault.ssg.util.ExtensionsUtil.stripExtension

@NullCheck
@EqualsAndHashCode(includeFields = true)
class SpecialPageFileSpecialPagesProvider implements SpecialPagesProvider, WithWatchableDir {

    private static final Logger logger = LoggerFactory.getLogger(SpecialPageFileSpecialPagesProvider)

    private final Collection<SpecialPageType> specialPageTypes
    private final File specialPagesDir

    SpecialPageFileSpecialPagesProvider(Collection<SpecialPageType> specialPageTypes, File specialPagesDir) {
        this.specialPageTypes = specialPageTypes
        this.specialPagesDir = specialPagesDir
        this.watchableDir = this.specialPagesDir
    }

    private SpecialPageType getSpecialPageType(File file) {
        def path = file.path
        this.specialPageTypes.find {
            it.ids.contains(getExtension(path))
        }
    }

    @Override
    Collection<SpecialPage> provide() {
        if (!this.specialPagesDir.isDirectory()) {
            logger.warn('specialPagesDir {} does not exist or is not a directory; skipping and providing no SpecialPages', this.specialPagesDir)
            []
        } else {
            def specialPages = []
            this.specialPagesDir.eachFileRecurse(FileType.FILES) {
                def type = this.getSpecialPageType(it)
                if (type != null) {
                    def relativePath = this.specialPagesDir.relativePath(it)
                    def path = stripExtension(relativePath)
                    logger.info('found specialPage {} with type {}', path, type)
                    specialPages << new SpecialPage(it.text, path, type)
                } else {
                    logger.warn('ignoring {} since there is no specialPageType for it', it)
                }
            }
            specialPages
        }
    }

    @Override
    Collection<SpecialPageType> getSpecialPageTypes() {
        this.specialPageTypes
    }

    @Override
    String toString() {
        "SpecialPageFileSpecialPagesProvider(specialPagesDir: ${ this.specialPagesDir }, specialPageTypes: ${ this.specialPageTypes })"
    }

}
