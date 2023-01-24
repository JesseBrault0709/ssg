package com.jessebrault.gcp.node

abstract class GcpNodeVisitor {

    void visit(GcpNode node) {
        this.visitChildren(node)
    }

    void visitChildren(GcpNode node) {
        node.children.each(this.&visit)
    }

}
