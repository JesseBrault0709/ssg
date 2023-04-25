package com.jessebrault.ssg.page

import com.jessebrault.ssg.provider.CollectionProvider
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.mockito.Mockito.mock

final class PagesProvidersTests {

    private static final PageType gspType = new PageType(['.gsp'], mock(PageRenderer))

    private File specialPagesDir
    private CollectionProvider<Page> pagesProvider

    @BeforeEach
    void beforeEach() {
        this.specialPagesDir = File.createTempDir()
        this.pagesProvider = PagesProviders.from(this.specialPagesDir, [gspType])
    }

    @Test
    void findsSpecialPage() {
        new FileTreeBuilder(this.specialPagesDir)
                .file('test.gsp', '<%= "Hello, World!" %>')

        def r = this.pagesProvider.provide()
        assertEquals(1, r.size())
        def f0 = r[0]
        assertEquals('test.gsp', f0.path)
        assertEquals('<%= "Hello, World!" %>', f0.text)
        assertEquals(gspType, f0.type)
    }

    @Test
    void findsNestedSpecialPage() {
        new FileTreeBuilder(this.specialPagesDir).dir('nested') {
            file('nested.gsp', '<%= "Hello, World!" %>')
        }

        def r = this.pagesProvider.provide()
        assertEquals(1, r.size())
        def f0 = r[0]
        assertEquals('nested/nested.gsp', f0.path)
        assertEquals('<%= "Hello, World!" %>', f0.text)
        assertEquals(gspType, f0.type)
    }

    @Test
    void ignoresUnsupportedFile() {
        new FileTreeBuilder(this.specialPagesDir).file('.ignored', 'Ignored!')

        def r = this.pagesProvider.provide()
        assertEquals(0, r.size())
    }

}
