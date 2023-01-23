package com.jessebrault.gcp.component.node

class DollarScriptletValue extends Node {

    String scriptlet

    @Override
    String toString() {
        "DollarScriptletValue(${ this.scriptlet })"
    }

}
