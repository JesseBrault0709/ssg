package com.jessebrault.ssg.view

import org.jsoup.Jsoup

trait WithHtmlHelpers {

    String prettyFormat(String html) {
        Jsoup.parse(html).with {
            outputSettings().prettyPrint(true)
            toString()
        }
    }

}
