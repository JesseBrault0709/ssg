package com.jessebrault.gcp.component.node

abstract class ComponentNodeVisitor {

    void visit(ComponentNode node) {
        this.visitChildren(node)
    }

    void visitChildren(ComponentNode node) {
        node.children.each {
            this.visit(it)
        }
    }

}
