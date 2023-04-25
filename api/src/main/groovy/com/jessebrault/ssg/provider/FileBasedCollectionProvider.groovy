package com.jessebrault.ssg.provider

import groovy.io.FileType
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.PackageScope
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.FromString
import org.jetbrains.annotations.Nullable
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@PackageScope
@NullCheck
@EqualsAndHashCode(includeFields = true)
final class FileBasedCollectionProvider<T> extends AbstractCollectionProvider<T> {

    private static final Logger logger = LoggerFactory.getLogger(FileBasedCollectionProvider)

    private final File dir
    private final Closure<@Nullable T> fileToElementClosure

    FileBasedCollectionProvider(
            File dir,
            @ClosureParams(value = FromString, options = 'java.io.File')
            Closure<@Nullable T> fileToElementClosure
    ) {
        this.dir = dir
        this.fileToElementClosure = fileToElementClosure
    }

    @Override
    Collection<T> provide() {
        if (!this.dir.isDirectory()) {
            logger.error('{} does not exist or is not a directory; returning empty collection', this.dir)
            []
        } else {
            def ts = []
            this.dir.eachFileRecurse(FileType.FILES) {
                def t = this.fileToElementClosure(it)
                if (t) {
                    ts << t
                }
            }
            ts
        }
    }

}
