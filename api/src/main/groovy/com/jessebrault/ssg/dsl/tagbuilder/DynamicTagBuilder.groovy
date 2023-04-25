package com.jessebrault.ssg.dsl.tagbuilder

import org.codehaus.groovy.runtime.InvokerHelper

final class DynamicTagBuilder implements TagBuilder {

    @Override
    String create(String name) {
        "<$name />"
    }

    @Override
    String create(String name, Map<String, ?> attributes) {
        def formattedAttributes = attributes.collect {
            if (it.value instanceof String) {
                it.key + '="' + it.value + '"'
            } else if (it.value instanceof Integer) {
                it.key + '=' + it.value
            } else if (it.value instanceof Boolean && it.value == true) {
                it.key
            } else {
                it.key + '="' + it.value.toString() + '"'
            }
        }.join(' ')
        "<$name $formattedAttributes />"
    }

    @Override
    String create(String name, String body) {
        "<$name>$body</$name>"
    }

    @Override
    String create(String name, Map<String, ?> attributes, String body) {
        def formattedAttributes = attributes.collect {
            if (it.value instanceof String) {
                it.key + '="' + it.value + '"'
            } else if (it.value instanceof Integer) {
                it.key + '=' + it.value
            } else if (it.value instanceof Boolean && it.value == true) {
                it.key
            } else {
                it.key + '="' + it.value.toString() + '"'
            }
        }.join(' ')
        "<$name $formattedAttributes>$body</$name>"
    }

    @Override
    Object invokeMethod(String name, Object args) {
        def argsList = InvokerHelper.asList(args)
        return switch (argsList.size()) {
            case 0 -> this.create(name)
            case 1 -> {
                def arg0 = argsList[0]
                if (arg0 instanceof Map) {
                    this.create(name, arg0)
                } else if (arg0 instanceof String) {
                    this.create(name, arg0)
                } else {
                    throw new MissingMethodException(name, this.class, args, false)
                }
            }
            case 2 -> {
                def arg0 = argsList[0]
                def arg1 = argsList[1]
                if (arg0 instanceof Map && arg1 instanceof String) {
                    this.create(name, arg0, arg1)
                } else {
                    throw new MissingMethodException(name, this.class, args, false)
                }
            }
            default -> throw new MissingMethodException(name, this.class, args, false)
        }
    }

}
