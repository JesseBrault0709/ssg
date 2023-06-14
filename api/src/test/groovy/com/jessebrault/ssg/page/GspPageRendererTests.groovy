package com.jessebrault.ssg.page

import com.jessebrault.ssg.dsl.StandardDslConsumerTests
import com.jessebrault.ssg.render.RenderContext
import com.jessebrault.ssg.util.Result

final class GspPageRendererTests implements StandardDslConsumerTests {

    private static GspPageRenderer getRenderer(
            ClassLoader parentClassLoader = GspPageRendererTests.classLoader
    ) {
        new GspPageRenderer(parentClassLoader)
    }

    @Override
    Result<String> render(String scriptlet, RenderContext context, ClassLoader classLoader) {
        def renderer = getRenderer(classLoader)
        renderer.render(
                new Page('', new PageType([], renderer), scriptlet),
                context
        )
    }

}
