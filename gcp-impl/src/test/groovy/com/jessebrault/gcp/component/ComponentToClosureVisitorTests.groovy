package com.jessebrault.gcp.component

import com.jessebrault.gcp.component.node.ComponentRoot
import com.jessebrault.gcp.component.node.GStringValue
import com.jessebrault.gcp.component.node.KeyAndValue
import com.jessebrault.gcp.component.node.KeysAndValues
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

class ComponentToClosureVisitorTests {

    @Test
    void withEmptyKeysAndValues() {
        def cn = new ComponentRoot().tap {
            it.children << new KeysAndValues()
        }
        def v = new ComponentToClosureVisitor()
        v.visit(cn)
        assertEquals('{ attr { }; };', v.result)
    }

    @Test
    void withGStringKeyAndValue() {
        def cn = new ComponentRoot().tap {
            it.children << new KeysAndValues().tap {
                it.children << new KeyAndValue().tap {
                    key = 'greeting'
                    it.children << new GStringValue().tap {
                        gString = 'Hello, ${ frontMatter.person }!'
                    }
                }
            }
        }
        def v = new ComponentToClosureVisitor()
        v.visit(cn)
        assertEquals('{ attr { greeting = "Hello, ${ frontMatter.person }!"; }; };', v.result)
    }

}
