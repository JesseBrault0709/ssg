package com.jessebrault.ssg.part

import com.jessebrault.ssg.Diagnostic

interface PartRenderer {
    Tuple2<Collection<Diagnostic>, String> render(Part part, Map binding, Map globals)
}
