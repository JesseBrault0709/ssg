package com.jessebrault.gcp.component

import groovy.transform.TupleConstructor

@TupleConstructor
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
        GROOVY,
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

    @Override
    String toString() {
        "ComponentToken(${ this.type }, ${ this.text })"
    }

}
