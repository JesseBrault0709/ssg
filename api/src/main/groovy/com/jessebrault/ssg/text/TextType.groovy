package com.jessebrault.ssg.text

import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@TupleConstructor(defaults = false)
@NullCheck(includeGenerated = true)
@EqualsAndHashCode
final class TextType {

    final Collection<String> ids
    final TextRenderer renderer
    final FrontMatterGetter frontMatterGetter
    final ExcerptGetter excerptGetter

    @Override
    String toString() {
        "TextType(ids: ${ this.ids })"
    }

}
