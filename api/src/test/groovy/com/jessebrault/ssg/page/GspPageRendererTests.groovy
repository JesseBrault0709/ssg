package com.jessebrault.ssg.page

import com.jessebrault.ssg.dsl.StandardDslConsumerTests
import com.jessebrault.ssg.render.RenderContext
import com.jessebrault.ssg.util.Result

final class GspPageRendererTests implements StandardDslConsumerTests {

    private static GspPageRenderer getRenderer(
            ClassLoader classLoader,
            Collection<URL> urls
    ) {
        def tmpDir = File.createTempDir()
        def engine = new GroovyScriptEngine([tmpDir.toURI().toURL(), *urls] as URL[], classLoader)
        new GspPageRenderer(tmpDir, engine)
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
