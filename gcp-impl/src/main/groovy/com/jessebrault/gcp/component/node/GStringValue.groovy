package com.jessebrault.gcp.component.node

class GStringValue extends ComponentNode {

    String gString

    @Override
    String toString() {
        "GStringValue(${ this.gString })"
    }

}
