package com.jessebrault.gcp.component

import com.jessebrault.gcp.component.node.BooleanValue
import com.jessebrault.gcp.component.node.ComponentRoot
import com.jessebrault.gcp.component.node.DollarReferenceValue
import com.jessebrault.gcp.component.node.DollarScriptletValue
import com.jessebrault.gcp.component.node.ExpressionScriptletValue
import com.jessebrault.gcp.component.node.GStringValue
import com.jessebrault.gcp.component.node.KeyAndValue
import com.jessebrault.gcp.component.node.KeysAndValues
import com.jessebrault.gcp.component.node.ComponentNodeVisitor
import com.jessebrault.gcp.component.node.ScriptletValue
import com.jessebrault.gcp.component.node.StringValue

// NOT THREAD SAFE
class ComponentToClosureVisitor extends ComponentNodeVisitor {

    private StringBuilder b = new StringBuilder()

    String getResult() {
        b.toString()
    }

    void reset() {
        b = new StringBuilder()
    }

    void visit(ComponentRoot componentNode) {
        b << '{ '
        super.visit(componentNode)
        if (componentNode.body != null) {
            b << "bodyOut << ${ componentNode.body }; "
        }
        b << '};'
    }

    void visit(KeysAndValues keysAndValues) {
        b << 'attr { '
        super.visit(keysAndValues)
        b << '}; '
    }

    void visit(KeyAndValue keyAndValue) {
        b << "${ keyAndValue.key } = "
        super.visit(keyAndValue)
        b << '; '
    }

    void visit(GStringValue gStringValue) {
        b << "\"${ gStringValue.gString }\""
    }

    void visit(StringValue stringValue) {
        b << "'${ stringValue.string }'"
    }

    void visit(DollarReferenceValue dollarReferenceValue) {
        b << dollarReferenceValue.reference
    }

    void visit(DollarScriptletValue dollarScriptletValue) {
        b << dollarScriptletValue.scriptlet
    }

    void visit(ScriptletValue scriptletValue) {
        b << "render { out -> ${ scriptletValue.scriptlet } }"
    }

    void visit(ExpressionScriptletValue expressionScriptletValue) {
        b << expressionScriptletValue.scriptlet
    }

    void visit(BooleanValue booleanValue) {
        b << booleanValue.value.toString()
    }

}
