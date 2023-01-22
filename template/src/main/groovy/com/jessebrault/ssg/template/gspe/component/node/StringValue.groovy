package com.jessebrault.ssg.template.gspe.component.node

class StringValue extends Node {

    String string

    @Override
    String toString() {
        "StringValue(${ this.string })"
    }

}
