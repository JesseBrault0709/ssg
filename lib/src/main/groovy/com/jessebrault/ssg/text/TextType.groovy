package com.jessebrault.ssg.text

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@TupleConstructor(defaults = false)
@NullCheck
@EqualsAndHashCode
class TextType {

    Collection<String> ids
    TextRenderer renderer
    FrontMatterGetter frontMatterGetter

    @Override
    String toString() {
        "TextType(ids: ${ this.ids }, renderer: ${ this.renderer }, frontMatterGetter: ${ this.renderer })"
    }

}
