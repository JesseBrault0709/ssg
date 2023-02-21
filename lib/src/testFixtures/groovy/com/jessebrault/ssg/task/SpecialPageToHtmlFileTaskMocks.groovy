package com.jessebrault.ssg.task

import static com.jessebrault.ssg.specialpage.SpecialPageMocks.blankSpecialPage

final class SpecialPageToHtmlFileTaskMocks {

    static SpecialPageToHtmlFileTask blankSpecialPageToHtmlFileTask() {
        new SpecialPageToHtmlFileTask(
                '',
                blankSpecialPage(),
                new HtmlFileOutput(
                        new File(''),
                        '',
                        { '' }
                )
        )
    }

}
