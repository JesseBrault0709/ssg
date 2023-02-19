package com.jessebrault.ssg.testutil

import com.jessebrault.ssg.Diagnostic
import com.jessebrault.ssg.text.ExcerptGetter
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.FirstParam

import static org.junit.jupiter.api.Assertions.assertInstanceOf
import static org.junit.jupiter.api.Assertions.assertTrue

class DiagnosticsUtil {

    static Closure<String> getDiagnosticsMessageSupplier(Collection<Diagnostic> diagnostics) {
        return {
            diagnostics.collect {
                def writer = new StringWriter()
                it.exception.printStackTrace(new PrintWriter(writer))
                def stackTrace = writer.toString()
                "$it.message\n$stackTrace"
            }.join('\n')
        }
    }

    static void assertEmptyDiagnostics(Tuple2<Collection<Diagnostic>, ?> result) {
        assertTrue(result.v1.isEmpty(), getDiagnosticsMessageSupplier(result.v1))
    }

    static <E extends Exception> void assertDiagnosticException(
            Class<E> expectedException,
            Diagnostic diagnostic,
            @ClosureParams(FirstParam.FirstGenericType)
            Closure<Void> additionalAssertions = null
    ) {
        assertInstanceOf(expectedException, diagnostic.exception, {
            "Incorrect diagnostic exception class; message: ${ diagnostic.message }"
        })
        if (additionalAssertions) {
            additionalAssertions(diagnostic.exception)
        }
    }

}
