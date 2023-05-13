package com.jessebrault.ssg.buildscript

import com.jessebrault.ssg.page.PageType
import com.jessebrault.ssg.part.PartType
import com.jessebrault.ssg.template.TemplateType
import com.jessebrault.ssg.text.TextType
import com.jessebrault.ssg.util.Monoid
import com.jessebrault.ssg.util.Monoids
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@TupleConstructor(defaults = false)
@NullCheck(includeGenerated = true)
@EqualsAndHashCode
final class TypesContainer {

    static final Monoid<TypesContainer> DEFAULT_MONOID = Monoids.of(getEmpty(), TypesContainer::concat)

    static TypesContainer getEmpty() {
        new TypesContainer([], [], [], [])
    }

    static TypesContainer get(Map<String, Object> args) {
        new TypesContainer(
                args.textTypes ? args.textTypes as Collection<TextType> : [],
                args.pageTypes ? args.pageTypes as Collection<PageType> : [],
                args.templateTypes ? args.templateTypes as Collection<TemplateType> : [],
                args.partTypes ? args.partTypes as Collection<PartType> : []
        )
    }

    static TypesContainer concat(TypesContainer tc0, TypesContainer tc1) {
        new TypesContainer(
                tc0.textTypes + tc1.textTypes,
                tc0.pageTypes + tc1.pageTypes,
                tc0.templateTypes + tc1.templateTypes,
                tc0.partTypes + tc1.partTypes
        )
    }

    final Collection<TextType> textTypes
    final Collection<PageType> pageTypes
    final Collection<TemplateType> templateTypes
    final Collection<PartType> partTypes

    TypesContainer plus(TypesContainer other) {
        concat(this, other)
    }

    @Override
    String toString() {
        "TypesContainer(textTypes: ${ this.textTypes }, pageTypes: ${ this.pageTypes }, " +
                "templateTypes: ${ this.templateTypes }, partTypes: ${ this.partTypes })"
    }

}
