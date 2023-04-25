package com.jessebrault.ssg.text

import com.jessebrault.ssg.util.Result

interface ExcerptGetter {
    Result<String> getExcerpt(Text text, int limit)
}