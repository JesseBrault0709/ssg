package com.jessebrault.ssg.template.gspe.component.node

class DollarReferenceValue extends Node {

    String reference

    @Override
    String toString() {
        "DollarReferenceValue(${ this.reference })"
    }

}
