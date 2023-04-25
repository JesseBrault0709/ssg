package com.jessebrault.ssg.text

import org.jetbrains.annotations.Nullable
import org.junit.jupiter.api.Test

import java.util.function.Supplier

import static com.jessebrault.ssg.text.TextMocks.nonRenderableText
import static com.jessebrault.ssg.util.DiagnosticsUtil.assertEmptyDiagnostics
import static org.junit.jupiter.api.Assertions.assertEquals

abstract class AbstractExcerptGetterTests {

    protected abstract ExcerptGetter getExcerptGetter()

    protected final void doTest(int limit, String expected, Supplier<@Nullable Text> textSupplier) {
        def text = textSupplier.get()
        if (text) {
            def result = this.getExcerptGetter().getExcerpt(text, limit)
            assertEmptyDiagnostics(result)
            assertEquals(expected, result.get())
        }
    }

    @Test
    final void takesAllIfTextLessThanLimit() {
        this.doTest(10, 'One Two Three Four Five') {
            nonRenderableText('One Two Three Four Five')
        }
    }

    @Test
    final void takesTheLimit() {
        this.doTest(2, 'One Two') {
            nonRenderableText('One Two Three Four Five')
        }
    }

}
