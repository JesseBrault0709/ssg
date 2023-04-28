package com.jessebrault.ssg.provider

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

}
