package com.jessebrault.ssg

interface StaticSiteGenerator {
    void generate(File buildDir, Map globals)
}
