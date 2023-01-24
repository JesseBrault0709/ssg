package com.jessebrault.gcp.component

import com.jessebrault.gcp.component.node.*
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.FirstParam
import groovy.transform.stc.SimpleType
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static com.jessebrault.gcp.component.ComponentToken.Type.*

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue

class ComponentParserTests {

    private static final Logger logger = LoggerFactory.getLogger(ComponentParserTests)

    private static class NodeSpec<T extends ComponentNode> {

        Class<T> nodeClass
        Closure<Void> tests

        NodeSpec(
                Class<T> nodeClass,
                @DelegatesTo(value = NodeTester, strategy = Closure.DELEGATE_FIRST)
                @ClosureParams(FirstParam.FirstGenericType)
                Closure<Void> tests = null
        ) {
            this.nodeClass = Objects.requireNonNull(nodeClass)
            this.tests = tests
        }

        void test(ComponentNode actual) {
            logger.debug('actual: {}', actual)
            assertTrue(nodeClass.isAssignableFrom(actual.class))
            if (this.tests != null) {
                def nodeTester = new NodeTester()
                this.tests.setDelegate(nodeTester)
                this.tests.setResolveStrategy(Closure.DELEGATE_FIRST)
                this.tests(actual)

                def childIterator = actual.children.iterator()
                assertEquals(nodeTester.childSpecs.size(), actual.children.size())

                nodeTester.childSpecs.each {
                    assertTrue(childIterator.hasNext())
                    def next = childIterator.next()
                    it.test(next)
                }
            }
        }

        @Override
        String toString() {
            "NodeSpec(${ this.nodeClass.simpleName })"
        }

    }

    private static class NodeTester {

        List<NodeSpec<? extends ComponentNode>> childSpecs = []

        def <T extends ComponentNode> void expect(
                Class<T> childNodeClass,
                @DelegatesTo(value = NodeTester, strategy = Closure.DELEGATE_FIRST)
                @ClosureParams(FirstParam.FirstGenericType)
                Closure<Void> furtherTests
        ) {
            this.childSpecs << new NodeSpec<T>(childNodeClass, furtherTests)
        }

        void expect(Class nodeClass) {
            this.childSpecs << new NodeSpec(nodeClass)
        }

    }

    private final ComponentParser parser = new ComponentParser()

    private void selfClosing(
            Queue<ComponentToken> tokens,
            @DelegatesTo(value = NodeTester, strategy = Closure.DELEGATE_FIRST)
            @ClosureParams(value = SimpleType, options = ['com.jessebrault.gcp.component.node.ComponentNode'])
            Closure<Void> tests
    ) {
        def componentNode = this.parser.parse(tokens)
        logger.debug('componentNode: {}', componentNode)

        def componentSpec = new NodeSpec(ComponentRoot, tests)
        logger.debug('nodeSpec: {}', componentSpec)
        componentSpec.test(componentNode)
    }

    @Test
    void selfClosingNoKeysOrValues() {
        this.selfClosing(new LinkedList<>([
                new ComponentToken(LT),
                new ComponentToken(IDENTIFIER, 'Test'),
                new ComponentToken(FORWARD_SLASH),
                new ComponentToken(GT)
        ])) {
            assertEquals('Test', it.identifier)
            expect(KeysAndValues) {
                assertEquals(0, it.children.size())
            }
        }
    }

    @Test
    void selfClosingWithGStringValue() {
        this.selfClosing(new LinkedList<>([
                new ComponentToken(LT),
                new ComponentToken(IDENTIFIER, 'Test'),
                new ComponentToken(KEY, 'test'),
                new ComponentToken(EQUALS),
                new ComponentToken(DOUBLE_QUOTE),
                new ComponentToken(STRING, 'Hello, World!'),
                new ComponentToken(DOUBLE_QUOTE),
                new ComponentToken(FORWARD_SLASH),
                new ComponentToken(GT)
        ])) {
            assertEquals('Test', it.identifier)
            expect(KeysAndValues) {
                expect(KeyAndValue) {
                    assertEquals('test', it.key)
                    expect(GStringValue) {
                        assertEquals('Hello, World!', it.gString)
                    }
                }
            }
        }
    }

}
