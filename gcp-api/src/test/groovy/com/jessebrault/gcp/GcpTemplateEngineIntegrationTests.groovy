package com.jessebrault.gcp


import groovy.text.TemplateEngine
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

class GcpTemplateEngineIntegrationTests {

    private final TemplateEngine engine = new GcpTemplateEngine(new GcpTemplateEngine.Configuration(
            { new GcpTemplate() },
            [],
            []
    ))

    @Test
    void doctype() {
        def src = '<!DOCTYPE html>'
        def r = this.engine.createTemplate(src).make().toString()
        assertEquals('<!DOCTYPE html>', r)
    }

    @Test
    void handlesNewlines() {
        def src = '<!DOCTYPE html>\n<html>'
        def r = this.engine.createTemplate(src).make().toString()
        assertEquals(src, r)
    }

    @Test
    void emptyScriptlet() {
        def src = '<%%>'
        def r = this.engine.createTemplate(src).make().toString()
        assertEquals('', r)
    }

    @Test
    void simpleOut() {
        def src = '<% out << "Hello, World!" %>'
        def r = this.engine.createTemplate(src).make().toString()
        assertEquals('Hello, World!', r)
    }

    @Test
    void scriptletInString() {
        def src = '<html lang="<% out << "en" %>">'
        def r = this.engine.createTemplate(src).make().toString()
        assertEquals('<html lang="en">', r)
    }

    @Test
    void expressionScriptlet() {
        def src = '<%= 13 %>'
        def r = this.engine.createTemplate(src).make().toString()
        assertEquals('13', r)
    }

    @Test
    void bindingWorks() {
        def src = '<%= greeting %>'
        def r = this.engine.createTemplate(src).make([greeting: 'Hello, World!']).toString()
        assertEquals('Hello, World!', r)
    }

    static class CustomBaseTemplate extends GcpTemplate {

        def greeting = 'Greetings!'
        def name = 'Jesse'

        @SuppressWarnings('GrMethodMayBeStatic')
        def greet() {
            'Hello, World!'
        }

    }

    @Test
    void baseTemplateMethodsPresent() {
        def src = '<%= greet() %>'
        def configuration = new GcpTemplateEngine.Configuration({ new CustomBaseTemplate() }, [], [])
        def engine = new GcpTemplateEngine(configuration)
        def r = engine.createTemplate(src).make().toString()
        assertEquals('Hello, World!', r)
    }

    @Test
    void baseTemplatePropertiesPresent() {
        def src = '<%= this.greeting %>'
        def configuration = new GcpTemplateEngine.Configuration({ new CustomBaseTemplate() }, [], [])
        def engine = new GcpTemplateEngine(configuration)
        def r = engine.createTemplate(src).make().toString()
        assertEquals('Greetings!', r)
    }

    @Test
    void bindingOverridesCustomBaseTemplate() {
        def src = '<%= greet() %>'
        def configuration = new GcpTemplateEngine.Configuration({ new CustomBaseTemplate() }, [], [])
        def engine = new GcpTemplateEngine(configuration)
        def r = engine.createTemplate(src).make([greet: { "Hello, Person!" }]).toString()
        assertEquals('Hello, Person!', r)
    }

    static class Greeter implements Component {

        @Override
        String render(Map<String, ?> attr, String body) {
            "${ attr.greeting }, ${ attr.person }!"
        }

    }

    @Test
    void selfClosingComponent() {
        def src = '<Greeter greeting="Hello" person="World" />'
        def configuration = new GcpTemplateEngine.Configuration({ new GcpTemplate() }, [], [new Greeter()])
        def engine = new GcpTemplateEngine(configuration)
        def r = engine.createTemplate(src).make().toString()
        assertEquals('Hello, World!', r)
    }

    @Test
    void componentWithGStringAttrValue() {
        def src = '<Greeter greeting="Hello" person="person number ${ 13 }" />'
        def configuration = new GcpTemplateEngine.Configuration({ new GcpTemplate() }, [], [new Greeter()])
        def engine = new GcpTemplateEngine(configuration)
        def r = engine.createTemplate(src).make().toString()
        assertEquals('Hello, person number 13!', r)
    }

    @Test
    void componentWithGStringAttrValueCanAccessBinding() {
        def src = '<Greeter greeting="Hello" person="person named ${ name }" />'
        def configuration = new GcpTemplateEngine.Configuration({ new GcpTemplate() }, [], [new Greeter()])
        def engine = new GcpTemplateEngine(configuration)
        def r = engine.createTemplate(src).make([name: 'Jesse']).toString()
        assertEquals('Hello, person named Jesse!', r)
    }

    @Test
    void componentWithGStringAttrValueCanAccessBaseTemplateMethod() {
        def src = '<Greeter greeting="Hello" person="person named ${ getName() }" />'
        def configuration = new GcpTemplateEngine.Configuration({ new CustomBaseTemplate() }, [], [new Greeter()])
        def engine = new GcpTemplateEngine(configuration)
        def r = engine.createTemplate(src).make().toString()
        assertEquals('Hello, person named Jesse!', r)
    }

    @Test
    void componentWithGStringAttrValueCanAccessBaseTemplateProperty() {
        def src = '<Greeter greeting="Hello" person="person named ${ this.name }" />'
        def configuration = new GcpTemplateEngine.Configuration({ new CustomBaseTemplate() }, [], [new Greeter()])
        def engine = new GcpTemplateEngine(configuration)
        def r = engine.createTemplate(src).make().toString()
        assertEquals('Hello, person named Jesse!', r)
    }

}
