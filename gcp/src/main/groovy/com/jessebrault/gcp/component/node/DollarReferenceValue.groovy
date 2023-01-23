package com.jessebrault.gcp.component.node

class DollarReferenceValue extends Node {

    String reference

    @Override
    String toString() {
        "DollarReferenceValue(${ this.reference })"
    }

}
