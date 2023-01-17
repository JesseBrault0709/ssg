package com.jessebrault.ssg.template.gspe

import groovy.text.Template

class GspeTemplate implements Template {

    Closure templateClosure
    ComponentsContainer components

    String renderComponent(String componentName, Closure configureComponentInstance) {
        Map<String, String> attr = [:]
        def bodyOut = new StringBuilder()
        configureComponentInstance(attr, bodyOut)

        def component = this.components[componentName]
        component.render(attr, bodyOut.toString())
    }

    @Override
    final Writable make() {
        this.make([:])
    }

    @Override
    final Writable make(Map binding) {
        def rehydrated = this.templateClosure.rehydrate(binding, this, this).asWritable()
        rehydrated.setResolveStrategy(Closure.DELEGATE_FIRST)
        rehydrated
    }

}