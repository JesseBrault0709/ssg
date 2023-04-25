package com.jessebrault.ssg.task

import com.jessebrault.ssg.html.TextToHtmlTask

import static com.jessebrault.ssg.template.TemplateMocks.blankTemplate
import static com.jessebrault.ssg.text.TextMocks.blankText

final class TextToHtmlTaskMocks {

    static TextToHtmlTask blankTextToHtmlTask() {
        new TextToHtmlTask('', TaskSpec.getEmpty(), blankText(), blankTemplate(), [], [], [])
    }

}
