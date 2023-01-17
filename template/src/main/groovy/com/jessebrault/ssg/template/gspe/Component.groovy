package com.jessebrault.ssg.template.gspe

interface Component {
    String render(Map<String, ?> attr, String body)
}