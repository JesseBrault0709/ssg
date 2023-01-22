package com.jessebrault.ssg.template.gspe.component.node

class ExpressionScriptletValue extends Node {

    String scriptlet

    @Override
    String toString() {
        "ExpressionScriptletValue(${ this.scriptlet })"
    }

}
