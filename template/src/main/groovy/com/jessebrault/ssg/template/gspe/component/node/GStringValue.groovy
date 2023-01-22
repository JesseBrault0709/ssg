package com.jessebrault.ssg.template.gspe.component.node

class GStringValue extends Node {

    String gString

    @Override
    String toString() {
        "GStringValue(${ this.gString })"
    }

}
