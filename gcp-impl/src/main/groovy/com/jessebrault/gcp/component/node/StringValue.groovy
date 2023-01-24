package com.jessebrault.gcp.component.node

class StringValue extends ComponentNode {

    String string

    @Override
    String toString() {
        "StringValue(${ this.string })"
    }

}
