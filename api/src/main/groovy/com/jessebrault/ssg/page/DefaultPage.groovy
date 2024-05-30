package com.jessebrault.ssg.page

import com.jessebrault.ssg.view.PageView
import groovy.transform.EqualsAndHashCode
import groovy.transform.NullCheck
import groovy.transform.TupleConstructor

@TupleConstructor(defaults = false)
@NullCheck
@EqualsAndHashCode
class DefaultPage implements Page {

    final String name
    final String path
    final String fileExtension
    final Class<? extends PageView> viewType
    final String templateResource

    @Override
    String toString() {
        "SimplePage(name: $name, path: $path, fileExtension: $fileExtension, templateResource: $templateResource)"
    }

}
