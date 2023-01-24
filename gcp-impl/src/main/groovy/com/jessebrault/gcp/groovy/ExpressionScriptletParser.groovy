package com.jessebrault.gcp.groovy

class ExpressionScriptletParser {

    static class Result {
        String fullMatch
        String scriptlet
    }

    static String parse(String input) {

    }

    static Result parseResult(String input) {
        def match = parse(input)
        match != null ? new Result().tap {
            fullMatch = match
            scriptlet = fullMatch.substring(3, fullMatch.length() - 2)
        } : null
    }

}
