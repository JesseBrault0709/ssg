package com.jessebrault.ssg

interface StaticSiteGenerator {
    Tuple2<Collection<Diagnostic>, Collection<GeneratedPage>> generate(Map globals)
}
