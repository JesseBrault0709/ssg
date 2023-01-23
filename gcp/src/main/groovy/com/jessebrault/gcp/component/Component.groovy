package com.jessebrault.gcp.component

interface Component {
    String render(Map<String, ?> attr, String body)
}