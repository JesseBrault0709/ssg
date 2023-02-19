package com.jessebrault.ssg

import com.jessebrault.ssg.task.Output

interface StaticSiteGenerator {
    Tuple2<Collection<Diagnostic>, Collection<Output>> generate(Build build)
}
