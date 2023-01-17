package com.jessebrault.ssg.template.gspe

import groovy.transform.PackageScope

@PackageScope
enum FsmState {
    HTML,
    SCRIPTLET,
    EXPRESSION_SCRIPTLET,
    DOLLAR,

    COMPONENT,

    COMPONENT_IDENTIFIER,

    COMPONENT_ATTR_KEY,
    COMPONENT_ATTR_VALUE_OPEN,

    COMPONENT_ATTR_VALUE_STRING,
    COMPONENT_ATTR_VALUE_STRING_CLOSE,

    COMPONENT_CLOSE
}
