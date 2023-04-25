package com.jessebrault.ssg.page

import static org.mockito.Mockito.mock

final class PageMocks {

    static Page blankPage() {
        def renderer = mock(PageRenderer)
        new Page(
                '',
                new PageType([], renderer),
                ''
        )
    }

    private PageMocks() {}

}
