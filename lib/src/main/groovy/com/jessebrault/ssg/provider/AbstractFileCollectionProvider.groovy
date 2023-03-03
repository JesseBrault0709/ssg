package com.jessebrault.ssg.provider

import groovy.io.FileType
import org.jetbrains.annotations.Nullable
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static com.jessebrault.ssg.util.ExtensionsUtil.getExtension

abstract class AbstractFileCollectionProvider<T> implements Provider<Collection<T>>, WithWatchableDir {

    private static final Logger logger = LoggerFactory.getLogger(AbstractFileCollectionProvider)

    protected final File dir

    AbstractFileCollectionProvider(File dir) {
        this.dir = Objects.requireNonNull(dir)
        this.watchableDir = dir
    }

    protected abstract @Nullable T transformFileToT(File file, String relativePath, String extension)

    @Override
    Collection<T> provide() {
        if (!this.dir.isDirectory()) {
            logger.warn('{} does not exist or is not a directory; skipping', this.dir)
            []
        } else {
            def ts = []
            this.dir.eachFileRecurse(FileType.FILES) {
                def t = transformFileToT(it, this.dir.relativePath(it), getExtension(it.path))
                if (t) {
                    ts << t
                }
            }
            ts
        }
    }

}
