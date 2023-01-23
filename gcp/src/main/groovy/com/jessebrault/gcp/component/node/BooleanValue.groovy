package com.jessebrault.gcp.component.node

class BooleanValue extends Node {
    boolean value

    @Override
    String toString() {
        "BooleanValue(${ this.value })"
    }

}
