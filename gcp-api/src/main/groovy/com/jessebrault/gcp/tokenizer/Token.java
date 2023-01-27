package com.jessebrault.gcp.tokenizer;

import java.util.Collection;

public interface Token {

    enum Type {
        TEXT,

        DOLLAR,
        GROOVY_REFERENCE,
        CURLY_OPEN,
        SCRIPTLET,
        CURLY_CLOSE,
        BLOCK_SCRIPTLET_OPEN,
        EXPRESSION_SCRIPTLET_OPEN,
        SCRIPTLET_CLOSE,

        CLASS_NAME,
        PACKAGE_NAME,
        DOT,

        WHITESPACE,

        KEY,
        EQUALS,

        DOUBLE_QUOTE,
        STRING,
        SINGLE_QUOTE,

        COMPONENT_START,
        FORWARD_SLASH,
        COMPONENT_END,
        ;

        boolean isAnyOf(Collection<Type> types) {
            return types.contains(this);
        }

    }

    Type getType();
    CharSequence getText();
    int getInputIndex();
    int getLine();
    int getCol();

}
