package com.jessebrault.ssg.template.gspe.component.node

class ScriptletValue extends Node {

    String scriptlet

    @Override
    String toString() {
        "ScriptletValue(${ this.scriptlet })"
    }

}
