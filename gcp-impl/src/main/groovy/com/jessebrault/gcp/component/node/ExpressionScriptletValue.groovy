package com.jessebrault.gcp.component.node

class ExpressionScriptletValue extends ComponentNode {

    String scriptlet

    @Override
    String toString() {
        "ExpressionScriptletValue(${ this.scriptlet })"
    }

}
