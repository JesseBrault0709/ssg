package com.jessebrault.gcp.component.node

class DollarScriptletValue extends ComponentNode {

    String scriptlet

    @Override
    String toString() {
        "DollarScriptletValue(${ this.scriptlet })"
    }

}
