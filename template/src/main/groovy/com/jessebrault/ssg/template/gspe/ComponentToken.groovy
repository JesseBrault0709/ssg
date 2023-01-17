package com.jessebrault.ssg.template.gspe

import groovy.transform.TupleConstructor

@TupleConstructor(defaults = false)
class ComponentToken {

    enum Type {
        LT,
        GT,
        IDENTIFIER,
        KEY,
        EQUALS,
        DOUBLE_QUOTE,
        SINGLE_QUOTE,
        STRING,
        DOLLAR,
        CURLY_OPEN,
        GROOVY,
        CURLY_CLOSE,
        GROOVY_IDENTIFIER,
        PERCENT,
        FORWARD_SLASH
        ;

        boolean isAnyOf(Collection<Type> types) {
            types.contains(this)
        }

    }

    Type type
    String text

}
