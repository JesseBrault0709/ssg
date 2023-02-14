package com.jessebrault.ssg.specialpage

import com.jessebrault.ssg.provider.AbstractFileCollectionProvider
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import org.jetbrains.annotations.Nullable
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@NullCheck
@EqualsAndHashCode(includeFields = true)
class SpecialPageFileSpecialPagesProvider extends AbstractFileCollectionProvider<SpecialPage>
        implements SpecialPagesProvider {

    private static final Logger logger = LoggerFactory.getLogger(SpecialPageFileSpecialPagesProvider)

    private final Collection<SpecialPageType> specialPageTypes

    SpecialPageFileSpecialPagesProvider(File specialPagesDir, Collection<SpecialPageType> specialPageTypes) {
        super(specialPagesDir)
        this.specialPageTypes = Objects.requireNonNull(specialPageTypes)
    }

    private @Nullable SpecialPageType getSpecialPageType(String extension) {
        this.specialPageTypes.find {
            it.ids.contains(extension)
        }
    }

    @Override
    protected @Nullable SpecialPage transformFileToT(File file, String relativePath, String extension) {
        def specialPageType = getSpecialPageType(extension)
        if (!specialPageType) {
            logger.warn('there is no SpecialPageType for {}, ignoring', relativePath)
        }
        specialPageType ? new SpecialPage(file.text, relativePath, specialPageType) : null
    }

    @Override
    Collection<SpecialPageType> getSpecialPageTypes() {
        this.specialPageTypes
    }

    @Override
    String toString() {
        "SpecialPageFileSpecialPagesProvider(specialPagesDir: ${ this.dir }, " +
                "specialPageTypes: ${ this.specialPageTypes })"
    }

}
