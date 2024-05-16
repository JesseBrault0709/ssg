package com.jessebrault.ssg.di

import groowt.util.di.DefaultRegistryObjectFactory
import groowt.util.di.RegistryObjectFactory

final class SsgObjectFactory {

    static RegistryObjectFactory getDefault() {
        DefaultRegistryObjectFactory.Builder.withDefaults().with {
            it.configureRegistry { registry ->
                registry.addExtension(new PagesExtension())
            }
            build()
        }
    }

    private SsgObjectFactory() {}

}
