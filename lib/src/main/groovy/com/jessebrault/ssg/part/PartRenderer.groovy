package com.jessebrault.ssg.part

interface PartRenderer {
    String render(String partText, Map binding, Map globals)
}
