package com.jessebrault.ssg

import com.jessebrault.ssg.part.PartsProvider
import com.jessebrault.ssg.specialpage.SpecialPagesProvider
import com.jessebrault.ssg.template.TemplatesProvider
import com.jessebrault.ssg.text.TextsProvider
import groovy.transform.Canonical
import groovy.transform.MapConstructor
import groovy.transform.NullCheck

@Canonical
@MapConstructor
@NullCheck
class Config {
    Collection<TextsProvider> textProviders
    Collection<TemplatesProvider> templatesProviders
    Collection<PartsProvider> partsProviders
    Collection<SpecialPagesProvider> specialPagesProviders
}
