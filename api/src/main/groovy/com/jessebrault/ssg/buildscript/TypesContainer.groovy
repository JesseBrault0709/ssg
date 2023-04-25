package com.jessebrault.ssg.buildscript

import com.jessebrault.ssg.page.PageType
import com.jessebrault.ssg.part.PartType
import com.jessebrault.ssg.template.TemplateType
import com.jessebrault.ssg.text.TextType
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@TupleConstructor(defaults = false)
@NullCheck(includeGenerated = true)
@EqualsAndHashCode
final class TypesContainer {

    static TypesContainer getEmpty() {
        new TypesContainer([], [], [], [])
    }

    static TypesContainer concat(TypesContainer tc0, TypesContainer tc1) {
        new TypesContainer(
                tc0.textTypes + tc1.textTypes,
                tc0.pageTypes + tc1.pageTypes,
                tc0.templateTypes + tc1.templateTypes,
                tc0.partTypes + tc1.partTypes
        )
    }

    Collection<TextType> textTypes
    Collection<PageType> pageTypes
    Collection<TemplateType> templateTypes
    Collection<PartType> partTypes

    TypesContainer plus(TypesContainer other) {
        concat(this, other)
    }

}
