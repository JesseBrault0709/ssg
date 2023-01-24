package com.jessebrault.gcp

import com.jessebrault.gcp.groovy.BlockScriptletParser
import com.jessebrault.gcp.groovy.DollarReferenceParser
import com.jessebrault.gcp.groovy.DollarScriptletParser
import com.jessebrault.gcp.groovy.ExpressionScriptletParser
import com.jessebrault.gcp.node.Document
import com.jessebrault.gcp.node.DollarReference
import com.jessebrault.gcp.node.DollarScriptlet
import com.jessebrault.gcp.node.ExpressionScriptlet
import com.jessebrault.gcp.node.Html
import com.jessebrault.gcp.node.BlockScriptlet

import java.util.regex.Matcher
import java.util.regex.Pattern

class GcpParser {

//    private static enum State {
//        HTML, DOLLAR_GROOVY, SCRIPTLET, EXPRESSION_SCRIPTLET
//    }
//
//    private static FunctionFsmBuilder<String, State, String> getFsmBuilder() {
//        new FunctionFsmBuilderImpl<>()
//    }

    private static final Pattern html = ~/^(?:[\w\W&&[^<$]]|<(?![%\p{Lu}]))+/

    private static final Pattern groovyIdentifier = ~/^[a-zA-Z_\u0024\u00c0-\u00d6\u00d8-\u00f6\u00f8-\u00ff\u0100-\ufff3][a-zA-Z_\u00240-9\u00c0-\u00d6\u00d8-\u00f6\u00f8-\u00ff\u0100-\ufff3]*/


    static Document parse(String gcp) {

    }

    private static Document document(String gcp) {
        def document = new Document()
        def remaining = gcp
        while (remaining.length() > 0) {
            Matcher m
            DollarReferenceParser.Result dollarReferenceResult
            DollarScriptletParser.Result dollarScriptletResult
            BlockScriptletParser.Result blockScriptletResult
            ExpressionScriptletParser.Result expressionScriptletResult

            String match

            if ((m = html.matcher(remaining)).find()) {
                match = m.group()
                document.children << new Html().tap {
                    text = match
                }
            } else if (dollarReferenceResult = DollarReferenceParser.parse(remaining)) {
                match = dollarReferenceResult.fullMatch
                document.children << new DollarReference().tap {
                    reference = dollarReferenceResult.reference
                }
            } else if (dollarScriptletResult = DollarScriptletParser.parseResult(remaining)) {
                match = dollarScriptletResult.fullMatch
                document.children << new DollarScriptlet().tap {
                    scriptlet = dollarScriptletResult.scriptlet
                }
            } else if (blockScriptletResult = BlockScriptletParser.parseResult(remaining)) {
                match = blockScriptletResult.fullMatch
                document.children << new BlockScriptlet().tap {
                    scriptlet = blockScriptletResult.scriptlet
                }
            } else if (expressionScriptletResult = ExpressionScriptletParser.parseResult(remaining)) {
                match = expressionScriptletResult.fullMatch
                document.children << new ExpressionScriptlet().tap {
                    scriptlet = expressionScriptletResult.scriptlet
                }
            }

            remaining = remaining.substring(match.length())
        }
        document
    }

}
