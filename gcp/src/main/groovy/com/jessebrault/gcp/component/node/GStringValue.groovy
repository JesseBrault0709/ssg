package com.jessebrault.gcp.component.node

class GStringValue extends Node {

    String gString

    @Override
    String toString() {
        "GStringValue(${ this.gString })"
    }

}
