package com.jessebrault.gcp.component.node

class BooleanValue extends ComponentNode {
    boolean value

    @Override
    String toString() {
        "BooleanValue(${ this.value })"
    }

}
