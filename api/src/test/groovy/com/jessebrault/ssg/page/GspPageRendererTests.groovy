package com.jessebrault.ssg.page

import com.jessebrault.ssg.dsl.StandardDslConsumerTests
import com.jessebrault.ssg.render.RenderContext
import com.jessebrault.ssg.util.Result

final class GspPageRendererTests implements StandardDslConsumerTests {

    private final PageRenderer renderer = new GspPageRenderer()

    @Override
    Result<String> render(String scriptlet, RenderContext context) {
        this.renderer.render(
                new Page('', new PageType([], this.renderer), scriptlet),
                context
        )
    }

}
