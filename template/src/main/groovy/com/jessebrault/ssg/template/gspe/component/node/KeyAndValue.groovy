package com.jessebrault.ssg.template.gspe.component.node

class KeyAndValue extends Node {

    String key

    @Override
    String toString() {
        "KeyAndValue(key: ${ this.key }, children: ${ this.children })"
    }

}
