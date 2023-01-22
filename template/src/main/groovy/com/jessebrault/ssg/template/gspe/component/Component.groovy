package com.jessebrault.ssg.template.gspe.component

interface Component {
    String render(Map<String, ?> attr, String body)
}