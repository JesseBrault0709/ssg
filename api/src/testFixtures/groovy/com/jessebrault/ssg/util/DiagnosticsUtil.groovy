package com.jessebrault.ssg.util

import groovy.transform.stc.ClosureParams
import groovy.transform.stc.FirstParam

import static org.junit.jupiter.api.Assertions.assertInstanceOf
import static org.junit.jupiter.api.Assertions.assertTrue

final class DiagnosticsUtil {

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

    static void assertEmptyDiagnostics(Result<?> result) {
        assertTrue(!result.hasDiagnostics(), getDiagnosticsMessageSupplier(result.diagnostics))
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

    private DiagnosticsUtil() {}

}
