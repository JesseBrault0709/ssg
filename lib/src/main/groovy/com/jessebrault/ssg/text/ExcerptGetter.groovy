package com.jessebrault.ssg.text

import com.jessebrault.ssg.Diagnostic

interface ExcerptGetter {
    Tuple2<Collection<Diagnostic>, String> getExcerpt(Text text, int limit)
}