package com.jessebrault.ssg.template.gspe.component.node

class DollarScriptletValue extends Node {

    String scriptlet

    @Override
    String toString() {
        "DollarScriptletValue(${ this.scriptlet })"
    }

}
