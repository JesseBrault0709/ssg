package com.jessebrault.gcp.component.node

class DollarReferenceValue extends ComponentNode {

    String reference

    @Override
    String toString() {
        "DollarReferenceValue(${ this.reference })"
    }

}
