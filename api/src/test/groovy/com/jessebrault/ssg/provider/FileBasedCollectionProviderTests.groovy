package com.jessebrault.ssg.provider

import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertTrue

final class FileBasedCollectionProviderTests extends AbstractCollectionProviderTests {

    @Override
    protected CollectionProvider<Integer> getCollectionProvider(Collection<Integer> ts) {
        def baseDir = File.createTempDir()
        new FileTreeBuilder(baseDir).tap {
            ts.each {
                file(it.toString(), '')
            }
        }
        new FileBasedCollectionProvider<>(baseDir, { File file, String relativePath ->
            logger.debug('file: {}, relativePath: {}', file, relativePath)
            def r = Integer.valueOf(relativePath)
            logger.debug('r: {}', r)
            r
        })
    }

    @Test
    void containsSelfType() {
        def p = new FileBasedCollectionProvider<String>(new File(''), { f -> '' })
        assertTrue(p.containsType(DirectoryCollectionProvider))
    }

}
