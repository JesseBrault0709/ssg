package com.jessebrault.gcp.component

import com.jessebrault.gcp.component.node.BooleanValue
import com.jessebrault.gcp.component.node.ComponentNode
import com.jessebrault.gcp.component.node.DollarReferenceValue
import com.jessebrault.gcp.component.node.DollarScriptletValue
import com.jessebrault.gcp.component.node.ExpressionScriptletValue
import com.jessebrault.gcp.component.node.GStringValue
import com.jessebrault.gcp.component.node.KeyAndValue
import com.jessebrault.gcp.component.node.KeysAndValues
import com.jessebrault.gcp.component.node.NodeVisitor
import com.jessebrault.gcp.component.node.ScriptletValue
import com.jessebrault.gcp.component.node.StringValue

// NOT THREAD SAFE, and must be used exactly once
class ComponentToScriptVisitor extends NodeVisitor {

    private final Writer w = new StringWriter()
    private final IndentPrinter p = new IndentPrinter(this.w, '    ', true, true)

    String getResult() {
        w.toString()
    }

    void visit(ComponentNode componentNode) {
        p.println('{ attr, bodyOut ->')
        p.incrementIndent()
        super.visit(componentNode)
        if (componentNode.body != null) {
            p.println("bodyOut << ${ componentNode.body };")
        }
        p.decrementIndent()
        p.println('};')
    }

    void visit(KeysAndValues keysAndValues) {
        p.println('attr {')
        p.incrementIndent()
        super.visit(keysAndValues)
        p.decrementIndent()
        p.println('};')
    }

    void visit(KeyAndValue keyAndValue) {
        p.printIndent()
        p.print("${ keyAndValue.key } = ")
        super.visit(keyAndValue)
        p.print(';\n')
    }

    void visit(GStringValue gStringValue) {
        p.print("\"${ gStringValue.gString }\"")
    }

    void visit(StringValue stringValue) {
        p.print("'${ stringValue.string }'")
    }

    void visit(DollarReferenceValue dollarReferenceValue) {
        p.print(dollarReferenceValue.reference)
    }

    void visit(DollarScriptletValue dollarScriptletValue) {
        p.print(dollarScriptletValue.scriptlet)
    }

    void visit(ScriptletValue scriptletValue) {
        p.println("render { out ->")
        p.incrementIndent()
        p.print(scriptletValue.scriptlet)
        p.decrementIndent()
        p.println('}')
    }

    void visit(ExpressionScriptletValue expressionScriptletValue) {
        p.print(expressionScriptletValue.scriptlet)
    }

    void visit(BooleanValue booleanValue) {
        p.print(booleanValue.value.toString())
    }

}
