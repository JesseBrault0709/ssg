package com.jessebrault.ssg.text

import com.jessebrault.ssg.Diagnostic

interface TextRenderer {
    Tuple2<Collection<Diagnostic>, String> render(Text text, Map globals)
}
