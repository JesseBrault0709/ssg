package com.jessebrault.ssg.page

import com.jessebrault.ssg.dsl.StandardDslConsumerTests
import com.jessebrault.ssg.render.RenderContext
import com.jessebrault.ssg.util.Result

final class GspPageRendererTests implements StandardDslConsumerTests {

    private static GspPageRenderer getRenderer(ClassLoader classLoader, Collection<URL> urls) {
        new GspPageRenderer(classLoader, urls)
    }

    @Override
    Result<String> render(String scriptlet, RenderContext context, ClassLoader classLoader, Collection<URL> urls) {
        def renderer = getRenderer(classLoader, urls)
        renderer.render(
                new Page('', new PageType([], renderer), scriptlet),
                context
        )
    }

}
