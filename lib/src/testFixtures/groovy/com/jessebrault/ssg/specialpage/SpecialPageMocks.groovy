package com.jessebrault.ssg.specialpage

import static org.mockito.Mockito.mock

final class SpecialPageMocks {

    static SpecialPage blankSpecialPage() {
        def renderer = mock(SpecialPageRenderer)
        new SpecialPage(
                '',
                '',
                new SpecialPageType([], renderer)
        )
    }

}
