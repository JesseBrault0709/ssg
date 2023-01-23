package com.jessebrault.gcp.component.node

class ExpressionScriptletValue extends Node {

    String scriptlet

    @Override
    String toString() {
        "ExpressionScriptletValue(${ this.scriptlet })"
    }

}
