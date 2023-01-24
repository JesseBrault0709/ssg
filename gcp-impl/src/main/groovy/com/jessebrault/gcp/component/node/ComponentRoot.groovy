package com.jessebrault.gcp.component.node

class ComponentRoot extends ComponentNode {

    String identifier
    String body

    @Override
    String toString() {
        "ComponentNode(identifier: ${ this.identifier }, body: ${ this.body }, children: ${ this.children })"
    }

}
