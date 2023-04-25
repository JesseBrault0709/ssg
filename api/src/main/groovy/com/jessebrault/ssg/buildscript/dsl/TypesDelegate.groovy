package com.jessebrault.ssg.buildscript.dsl

import com.jessebrault.ssg.buildscript.TypesContainer
import com.jessebrault.ssg.page.PageType
import com.jessebrault.ssg.part.PartType
import com.jessebrault.ssg.template.TemplateType
import com.jessebrault.ssg.text.TextType
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck

@NullCheck
@EqualsAndHashCode
final class TypesDelegate {

    final Collection<TextType> textTypes = []
    final Collection<PageType> pageTypes = []
    final Collection<TemplateType> templateTypes = []
    final Collection<PartType> partTypes = []

    TypesContainer getResult() {
        new TypesContainer(this.textTypes, this.pageTypes, this.templateTypes, this.partTypes)
    }

}
