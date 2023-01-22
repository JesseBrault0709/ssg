package com.jessebrault.ssg.template.gspe.component

import com.jessebrault.ssg.template.gspe.component.node.BooleanValue
import com.jessebrault.ssg.template.gspe.component.node.ComponentNode
import com.jessebrault.ssg.template.gspe.component.node.DollarReferenceValue
import com.jessebrault.ssg.template.gspe.component.node.DollarScriptletValue
import com.jessebrault.ssg.template.gspe.component.node.ExpressionScriptletValue
import com.jessebrault.ssg.template.gspe.component.node.GStringValue
import com.jessebrault.ssg.template.gspe.component.node.KeyAndValue
import com.jessebrault.ssg.template.gspe.component.node.KeysAndValues
import com.jessebrault.ssg.template.gspe.component.node.NodeVisitor
import com.jessebrault.ssg.template.gspe.component.node.ScriptletValue
import com.jessebrault.ssg.template.gspe.component.node.StringValue

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
