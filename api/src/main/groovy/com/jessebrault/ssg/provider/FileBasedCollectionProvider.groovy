package com.jessebrault.ssg.provider

import com.jessebrault.ssg.util.PathUtil
import groovy.io.FileType
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.PackageScope
import org.jetbrains.annotations.Nullable
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.function.BiFunction

@PackageScope
@NullCheck
@EqualsAndHashCode(includeFields = true, callSuper = true)
final class FileBasedCollectionProvider<T> extends AbstractCollectionProvider<T> {

    private static final Logger logger = LoggerFactory.getLogger(FileBasedCollectionProvider)

    private final File baseDirectory
    private final BiFunction<File, String, @Nullable T> elementFunction

    FileBasedCollectionProvider(File baseDirectory, BiFunction<File, String, T> elementFunction) {
        super([], [])
        this.baseDirectory = baseDirectory
        this.elementFunction = elementFunction
    }

    @Override
    Collection<T> provide() {
        if (!this.baseDirectory.isDirectory()) {
            logger.error('{} does not exist or is not a directory; returning empty collection', this.baseDirectory)
            []
        } else {
            final Collection<T> ts = []
            this.baseDirectory.eachFileRecurse(FileType.FILES) {
                def t = this.elementFunction.apply(it, PathUtil.relative(this.baseDirectory.path, it.path)) as T
                if (t != null) {
                    ts << t
                }
            }
            ts
        }
    }

}
