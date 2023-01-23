package com.jessebrault.gcp

import com.jessebrault.gcp.component.Component
import com.jessebrault.gcp.component.ComponentsContainer
import groovy.text.Template
import groovy.text.TemplateEngine
import groovy.transform.TupleConstructor
import org.codehaus.groovy.control.CompilationFailedException
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Supplier

final class GcpTemplateEngine extends TemplateEngine {

    private static final Logger logger = LoggerFactory.getLogger(GcpTemplateEngine)

    @TupleConstructor(defaults = false)
    static class Configuration {
        Supplier<GcpTemplate> ssgTemplateSupplier
        Collection<URL> componentDirUrls
        Collection<Component> components
    }

    private final Configuration configuration
    private final File scriptsDir = File.createTempDir()
    private final AtomicInteger templateCount = new AtomicInteger(0)
    private final GroovyScriptEngine scriptEngine

    GcpTemplateEngine(Configuration configuration) {
        this.configuration = configuration
        this.scriptEngine = new GroovyScriptEngine([this.scriptsDir.toURI().toURL()] as URL[])
    }

    @Override
    Template createTemplate(Reader reader) throws CompilationFailedException, ClassNotFoundException, IOException {
        def templateSrc = reader.text

        def converter = new TemplateToScriptConverter()
        def scriptSrc = converter.convert(templateSrc)
        logger.debug('scriptSrc: {}', scriptSrc)
        def scriptName = "SsgTemplate${ this.templateCount.getAndIncrement() }.groovy"
        new File(this.scriptsDir, scriptName).write(scriptSrc)

        def script = this.scriptEngine.createScript(scriptName, new Binding())

        def templateClosure = (Closure) script.invokeMethod('getTemplate', null)

        def template = this.configuration.ssgTemplateSupplier.get()
        template.templateClosure = templateClosure
        template.components = new ComponentsContainer(this.configuration.componentDirUrls, this.configuration.components)

        template
    }

}
