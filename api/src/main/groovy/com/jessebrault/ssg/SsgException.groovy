package com.jessebrault.ssg

class SsgException extends RuntimeException {

    SsgException(String message) {
        super(message)
    }

    SsgException(String message, Throwable cause) {
        super(message, cause)
    }

}
