package com.jessebrault.ssg.page

import com.jessebrault.ssg.util.Diagnostic
import com.jessebrault.ssg.view.PageView
import groowt.util.fp.either.Either

interface Page {

    String getName()
    String getPath()
    String getFileExtension()

    Either<Diagnostic, PageView> createView()

}
