package com.jessebrault.ssg.task

import static com.jessebrault.ssg.text.TextMocks.blankText

final class TextToHtmlFileTaskMocks {

    static TextToHtmlFileTask blankTextToHtmlFileTask() {
        new TextToHtmlFileTask(
                '',
                blankText(),
                new HtmlFileOutput(
                        new File(''),
                        '',
                        { '' }
                )
        )
    }

}
