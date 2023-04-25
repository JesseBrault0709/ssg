package com.jessebrault.ssg.task

import com.jessebrault.ssg.html.PageToHtmlTask

import static com.jessebrault.ssg.page.PageMocks.blankPage

final class PageToHtmlTaskMocks {

    static PageToHtmlTask blankPageToHtmlTask() {
        new PageToHtmlTask('', TaskSpec.getEmpty(), blankPage(), [], [], [])
    }

}
