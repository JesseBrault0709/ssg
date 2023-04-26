package com.jessebrault.ssg.dsl.tagbuilder

interface TagBuilder {
    String create(String name)
    String create(String name, Map<String, Object> attributes)
    String create(String name, String body)
    String create(String name, Map<String, Object> attributes, String body)
}
