package com.jessebrault.ssg

import com.jessebrault.ssg.output.OutputPage

interface StaticSiteGenerator {
    Tuple2<Collection<Diagnostic>, Collection<OutputPage>> generate(Build build)
}
