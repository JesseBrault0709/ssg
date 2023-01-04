package com.jessebrault.ssg

import com.jessebrault.ssg.pagetemplate.PageTemplateType
import com.jessebrault.ssg.pagetemplate.PageTemplatesFactory
import com.jessebrault.ssg.text.TextFileType
import com.jessebrault.ssg.text.TextFilesFactory
import groovy.transform.Canonical
import groovy.transform.MapConstructor

import java.util.function.Function

@Canonical
@MapConstructor
class Config {
    Collection<TextFileType> textFileTypes
    Collection<PageTemplateType> pageTemplateTypes

    Function<Config, TextFilesFactory> textFileFactoryGetter
    Function<Config, PageTemplatesFactory> pageTemplatesFactoryGetter
}
