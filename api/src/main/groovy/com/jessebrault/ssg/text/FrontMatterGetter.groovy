package com.jessebrault.ssg.text

import com.jessebrault.ssg.util.Result

interface FrontMatterGetter {
    Result<FrontMatter> get(Text text)
}
