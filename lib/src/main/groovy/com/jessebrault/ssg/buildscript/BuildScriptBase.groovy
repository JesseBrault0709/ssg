package com.jessebrault.ssg.buildscript

import com.jessebrault.ssg.Config
import com.jessebrault.ssg.part.PartType
import com.jessebrault.ssg.specialpage.SpecialPageType
import com.jessebrault.ssg.template.TemplateType
import com.jessebrault.ssg.text.TextType
import groovy.transform.TupleConstructor

abstract class BuildScriptBase extends Script {

    static class ConfigClosureDelegate {

        @Delegate
        private final Config config

        private final Collection<TextType> defaultTextTypes
        private final Collection<TemplateType> defaultTemplateTypes
        private final Collection<PartType> defaultPartTypes
        private final Collection<SpecialPageType> defaultSpecialPageTypes

        ConfigClosureDelegate(Config config) {
            this.config = config
            this.defaultTextTypes = this.config.textProviders.collectMany { it.textTypes }
            this.defaultTemplateTypes = this.config.templatesProviders.collectMany { it.templateTypes }
            this.defaultPartTypes = this.config.partsProviders.collectMany { it.partTypes }
            this.defaultSpecialPageTypes = this.config.specialPagesProviders.collectMany { it.specialPageTypes }
        }

        Collection<TextType> getDefaultTextTypes() {
            this.defaultTextTypes
        }

        Collection<TemplateType> getDefaultTemplateTypes() {
            this.defaultTemplateTypes
        }

        Collection<PartType> getDefaultPartTypes() {
            this.defaultPartTypes
        }

        Collection<SpecialPageType> getDefaultSpecialPageTypes() {
            this.defaultSpecialPageTypes
        }

    }

    @TupleConstructor(includeFields = true, defaults = false)
    static class GlobalsClosureDelegate {

        private final Map globals

        @Override
        Object getProperty(String propertyName) {
            this.globals[propertyName]
        }

        @Override
        void setProperty(String propertyName, Object newValue) {
            this.globals.put(propertyName, newValue)
        }

    }

    Config config
    Map globals

    void config(
            @DelegatesTo(value = ConfigClosureDelegate, strategy = Closure.DELEGATE_FIRST)
            Closure configClosure
    ) {
        configClosure.setDelegate(new ConfigClosureDelegate(this.config))
        configClosure.setResolveStrategy(Closure.DELEGATE_FIRST)
        configClosure.run()
    }

    void globals(
            @DelegatesTo(value = GlobalsClosureDelegate, strategy = Closure.DELEGATE_FIRST)
            Closure globalsClosure
    ) {
        def globalsConfigurator = new GlobalsClosureDelegate(this.globals)
        globalsClosure.setDelegate(globalsConfigurator)
        globalsClosure.setResolveStrategy(Closure.DELEGATE_FIRST)
        globalsClosure.run()
    }

}
