package com.jessebrault.ssg.testutil

import com.jessebrault.ssg.Diagnostic

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

}
