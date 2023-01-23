package com.jessebrault.gcp.component.node

class StringValue extends Node {

    String string

    @Override
    String toString() {
        "StringValue(${ this.string })"
    }

}
