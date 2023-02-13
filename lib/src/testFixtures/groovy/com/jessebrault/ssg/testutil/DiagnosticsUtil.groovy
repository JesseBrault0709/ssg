package com.jessebrault.ssg.testutil

import com.jessebrault.ssg.Diagnostic

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

}
