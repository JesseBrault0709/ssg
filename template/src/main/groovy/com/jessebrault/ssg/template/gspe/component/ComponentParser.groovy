package com.jessebrault.ssg.template.gspe.component

import com.jessebrault.ssg.template.gspe.component.ComponentToken.Type
import com.jessebrault.ssg.template.gspe.component.node.ComponentNode
import com.jessebrault.ssg.template.gspe.component.node.DollarReferenceValue
import com.jessebrault.ssg.template.gspe.component.node.DollarScriptletValue
import com.jessebrault.ssg.template.gspe.component.node.ExpressionScriptletValue
import com.jessebrault.ssg.template.gspe.component.node.GStringValue
import com.jessebrault.ssg.template.gspe.component.node.KeyAndValue
import com.jessebrault.ssg.template.gspe.component.node.KeysAndValues
import com.jessebrault.ssg.template.gspe.component.node.Node
import com.jessebrault.ssg.template.gspe.component.node.ScriptletValue
import com.jessebrault.ssg.template.gspe.component.node.StringValue
import groovy.transform.PackageScope

import static com.jessebrault.ssg.template.gspe.component.ComponentToken.Type.*

/**
 * NOT thread safe
 */
@PackageScope
class ComponentParser {

    private Queue<ComponentToken> tokens
    private String currentIdentifier

    ComponentNode parse(Queue<ComponentToken> tokens) {
        this.tokens = tokens
        this.selfClosingComponent()
    }

    String parse(Queue<ComponentToken> openingTokens, String bodyClosure, Queue<ComponentToken> closingTokens) {
        this.tokens = openingTokens
        def componentNode = this.openingComponent()

        componentNode.body = bodyClosure

        this.tokens = closingTokens
        this.closingComponent()

        componentNode
    }

    private static void error(Collection<Type> expectedTypes, ComponentToken actual) {
        throw new RuntimeException("expected ${ expectedTypes.join(' or ') } but got ${ actual ? "'${ actual }'" : 'null' }")
    }

    private ComponentToken expect(Collection<Type> types) {
        def t = this.tokens.poll()
        if (!t || !t.type.isAnyOf(types)) {
            error(types, t)
        }
        t
    }

    private ComponentToken expect(Type type) {
        this.expect([type])
    }

    private boolean peek(Type type) {
        def t = this.tokens.peek()
        t && t.type == type
    }

    private boolean peekSecond(Type type) {
        def t = this.tokens[1]
        t && t.type == type
    }

    private boolean peekThird(Type type) {
        def t = this.tokens[2]
        t && t.type == type
    }

    private ComponentNode selfClosingComponent() {
        this.startOfOpeningOrSelfClosingComponent()
        def keysAndValues = this.keysAndValues()
        this.expect(FORWARD_SLASH)
        this.expect(GT)
        new ComponentNode().tap {
            it.identifier = this.currentIdentifier
            it.children << keysAndValues
        }
    }

    private ComponentNode openingComponent() {
        this.startOfOpeningOrSelfClosingComponent()
        def keysAndValues = this.keysAndValues()
        this.expect(GT)
        new ComponentNode().tap {
            it.identifier = this.currentIdentifier
            it.children << keysAndValues
        }
    }

    private void closingComponent() {
        this.expect(LT)
        this.expect(FORWARD_SLASH)
        def identifierToken = this.expect(IDENTIFIER)
        if (identifierToken.text != this.currentIdentifier) {
            throw new RuntimeException("expected '${ this.currentIdentifier }' but got '${ t2.text }'")
        }
        this.expect(GT)
    }

    private void startOfOpeningOrSelfClosingComponent() {
        this.expect(LT)
        def identifierToken = this.expect(IDENTIFIER)
        this.currentIdentifier = identifierToken.text
    }

    private KeysAndValues keysAndValues() {
        List<Node> children = []
        while (true) {
            if (this.peek(KEY)) {
                def keyAndValue = this.keyAndValue()
                children << keyAndValue
            } else if (this.peek(FORWARD_SLASH)) {
                break
            } else {
                error([KEY, FORWARD_SLASH], this.tokens.poll())
            }
        }
        new KeysAndValues().tap {
            it.children.addAll(children)
        }
    }

    @PeekBefore(KEY)
    private KeyAndValue keyAndValue() {
        def keyToken = this.expect(KEY)
        this.expect(EQUALS)
        def value = this.value()
        new KeyAndValue().tap {
            key = keyToken.text
            it.children << value
        }
    }

    private Node value() {
        if (this.peek(DOUBLE_QUOTE)) {
            return this.doubleQuoteStringValue()
        } else if (this.peek(SINGLE_QUOTE)) {
            return this.singleQuoteStringValue()
        } else if (this.peek(GROOVY_IDENTIFIER)) {
            return this.dollarReferenceValue()
        } else if (this.peek(GROOVY)) {
            return this.dollarScriptletValue()
        } else if (this.peek(LT) && this.peekSecond(PERCENT) && this.peekThird(EQUALS)) {
            return this.expressionScriptletValue()
        } else if (this.peek(LT) && this.peekSecond(PERCENT)) {
            return this.scriptletValue()
        } else {
            error([DOUBLE_QUOTE, SINGLE_QUOTE, GROOVY_IDENTIFIER, GROOVY, LT], this.tokens.poll())
        }
        throw new RuntimeException('should not get here')
    }

    @PeekBefore(DOUBLE_QUOTE)
    private GStringValue doubleQuoteStringValue() {
        this.expect(DOUBLE_QUOTE)
        def stringToken = this.expect(STRING)
        this.expect(DOUBLE_QUOTE)
        new GStringValue().tap {
            gString = stringToken.text
        }
    }

    @PeekBefore(SINGLE_QUOTE)
    private StringValue singleQuoteStringValue() {
        this.expect(SINGLE_QUOTE)
        def stringToken = this.expect(STRING)
        this.expect(SINGLE_QUOTE)
        new StringValue().tap {
            string = stringToken.text
        }
    }

    @PeekBefore([GROOVY_IDENTIFIER])
    private DollarReferenceValue dollarReferenceValue() {
        def groovyIdentifierToken = this.expect(GROOVY_IDENTIFIER)
        new DollarReferenceValue().tap {
            reference = groovyIdentifierToken.text
        }
    }

    @PeekBefore([GROOVY])
    private DollarScriptletValue dollarScriptletValue() {
        def groovyToken = this.expect(GROOVY)
        new DollarScriptletValue().tap {
            scriptlet = groovyToken.text
        }
    }

    @PeekBefore([LT, PERCENT, EQUALS])
    private ExpressionScriptletValue expressionScriptletValue() {
        this.expect(LT)
        this.expect(PERCENT)
        this.expect(EQUALS)
        def groovyToken = this.expect(GROOVY)
        this.expect(PERCENT)
        this.expect(GT)
        new ExpressionScriptletValue().tap {
            scriptlet = groovyToken.text
        }
    }

    @PeekBefore([LT, PERCENT])
    private ScriptletValue scriptletValue() {
        this.expect(LT)
        this.expect(PERCENT)
        def groovyToken = this.expect(GROOVY)
        this.expect(PERCENT)
        this.expect(GT)
        new ScriptletValue().tap {
            scriptlet = groovyToken.text
        }
    }

}
