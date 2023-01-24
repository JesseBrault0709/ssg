package com.jessebrault.gcp

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.Marker
import org.slf4j.MarkerFactory

class ComponentsContainer {

    private static final Logger logger = LoggerFactory.getLogger(ComponentsContainer)
    private static final Marker enter = MarkerFactory.getMarker('ENTER')
    private static final Marker exit = MarkerFactory.getMarker('EXIT')

    private final Map<String, Component> componentCache = [:]
    private final GroovyClassLoader loader

    ComponentsContainer(Collection<URL> componentDirUrls, Collection<Component> components) {
        logger.trace(enter, 'componentDirUrls: {}, components: {}', componentDirUrls, components)
        this.loader = new GroovyClassLoader()
        componentDirUrls.each { this.loader.addURL(it) }
        components.each {
            this.componentCache[it.class.simpleName] = it
        }
        logger.debug('this.loader: {}', this.loader)
        logger.debug('this.componentCache: {}', this.componentCache)
        logger.trace(exit, '')
    }

    Component get(String name) {
        logger.trace('name: {}', name)
        def component = this.componentCache.computeIfAbsent(name, {
            def componentClass = (Class<? extends Component>) this.loader.loadClass(it)
            componentClass.getDeclaredConstructor().newInstance() // must be a default constructor (for now)
        })
        logger.trace(exit, 'component: {}', component)
        component
    }

    Component getAt(String name) {
        this.get(name)
    }

}
