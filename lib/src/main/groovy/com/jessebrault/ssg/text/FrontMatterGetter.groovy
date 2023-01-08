package com.jessebrault.ssg.text

import com.jessebrault.ssg.Diagnostic

interface FrontMatterGetter {
    Tuple2<Collection<Diagnostic>, FrontMatter> get(Text text)
}
