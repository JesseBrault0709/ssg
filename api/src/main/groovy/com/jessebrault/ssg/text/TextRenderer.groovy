package com.jessebrault.ssg.text

import com.jessebrault.ssg.util.Result

interface TextRenderer {
    Result<String> render(Text text)
}
