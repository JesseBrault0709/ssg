package com.jessebrault.ssg

import com.jessebrault.ssg.part.PartsProvider
import com.jessebrault.ssg.specialpage.SpecialPagesProvider
import com.jessebrault.ssg.template.TemplatesProvider
import com.jessebrault.ssg.text.TextsProvider
import groovy.transform.Canonical
import groovy.transform.EqualsAndHashCode
import groovy.transform.MapConstructor
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@TupleConstructor
@MapConstructor
@NullCheck
@EqualsAndHashCode
class Config {

    Collection<TextsProvider> textProviders
    Collection<TemplatesProvider> templatesProviders
    Collection<PartsProvider> partsProviders
    Collection<SpecialPagesProvider> specialPagesProviders

    String toString() {
        "Config(textProviders: ${ this.textProviders }, templatesProviders: ${ this.templatesProviders }, " +
                "partsProviders: ${ this.partsProviders }, specialPagesProviders: ${ this.specialPagesProviders })"
    }

}
