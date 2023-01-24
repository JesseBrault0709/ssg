package com.jessebrault.gcp.component.node

class ScriptletValue extends ComponentNode {

    String scriptlet

    @Override
    String toString() {
        "ScriptletValue(${ this.scriptlet })"
    }

}
