package com.jessebrault.ssg

import com.jessebrault.ssg.part.PartType
import com.jessebrault.ssg.part.PartsProvider
import com.jessebrault.ssg.template.TemplateType
import com.jessebrault.ssg.template.TemplatesProvider
import com.jessebrault.ssg.text.TextType
import com.jessebrault.ssg.text.TextsProvider
import groovy.transform.Canonical
import groovy.transform.MapConstructor
import groovy.transform.NullCheck

import java.util.function.Function

@Canonical
@MapConstructor
@NullCheck
class Config {
    Collection<TextType> textTypes
    Collection<TemplateType> templateTypes
    Collection<PartType> partTypes

    File textsDir
    File templatesDir
    File partsDir

    Function<Config, TextsProvider> textsProviderGetter
    Function<Config, TemplatesProvider> templatesProviderGetter
    Function<Config, PartsProvider> partsProviderGetter
}
