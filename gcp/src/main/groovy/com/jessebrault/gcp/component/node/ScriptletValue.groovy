package com.jessebrault.gcp.component.node

class ScriptletValue extends Node {

    String scriptlet

    @Override
    String toString() {
        "ScriptletValue(${ this.scriptlet })"
    }

}
