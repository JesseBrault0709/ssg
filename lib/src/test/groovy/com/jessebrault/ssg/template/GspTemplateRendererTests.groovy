package com.jessebrault.ssg.template

import com.jessebrault.ssg.part.GspPartRenderer
import com.jessebrault.ssg.part.Part
import com.jessebrault.ssg.part.PartType
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

class GspTemplateRendererTests {

    private final TemplateRenderer renderer = new GspTemplateRenderer()

    @Test
    void rendersPart() {
        def template = new Template(
                "<%= parts['greeting'].render([person: 'World']) %>",
                null,
                null
        )
        def part = new Part(
                'greeting',
                new PartType(['.gsp'], new GspPartRenderer()),
                'Hello, $person!'
        )
        def r = this.renderer.render(template, '', [part])
        assertEquals('Hello, World!', r)
    }

}
