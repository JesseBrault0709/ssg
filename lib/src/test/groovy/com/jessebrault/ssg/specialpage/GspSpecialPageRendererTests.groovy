package com.jessebrault.ssg.specialpage

import com.jessebrault.ssg.Diagnostic
import com.jessebrault.ssg.dsl.StandardDslConsumerTests
import com.jessebrault.ssg.renderer.RenderContext
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension)
class GspSpecialPageRendererTests implements StandardDslConsumerTests {

    private final SpecialPageRenderer renderer = new GspSpecialPageRenderer()

    @Override
    Tuple2<Collection<Diagnostic>, String> render(String scriptlet, RenderContext context) {
        this.renderer.render(
                new SpecialPage(scriptlet, '', new SpecialPageType([], this.renderer)),
                context
        )
    }

}
