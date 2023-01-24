package com.jessebrault.gcp

interface Component {
    String render(Map<String, ?> attr, String body)
}