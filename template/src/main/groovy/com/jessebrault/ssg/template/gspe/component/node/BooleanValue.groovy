package com.jessebrault.ssg.template.gspe.component.node

class BooleanValue extends Node {
    boolean value

    @Override
    String toString() {
        "BooleanValue(${ this.value })"
    }

}
