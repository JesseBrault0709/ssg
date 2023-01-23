package com.jessebrault.gcp.component.node

abstract class NodeVisitor {

    void visit(Node node) {
        this.visitChildren(node)
    }

    void visitChildren(Node node) {
        node.children.each {
            this.visit(it)
        }
    }

}
