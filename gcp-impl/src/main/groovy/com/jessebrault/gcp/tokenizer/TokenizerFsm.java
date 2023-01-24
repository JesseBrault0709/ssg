package com.jessebrault.gcp.tokenizer;

import com.jessebrault.fsm.function.FunctionFsm;
import com.jessebrault.fsm.function.FunctionFsmBuilder;
import com.jessebrault.fsm.function.FunctionFsmBuilderImpl;

import static com.jessebrault.gcp.tokenizer.Token.Type.*;

import java.util.regex.Pattern;

class TokenizerFsm {

    private static final PatternMatcher lessThan = new PatternMatcher(Pattern.compile("^<"));
    private static final PatternMatcher greaterThan = new PatternMatcher(Pattern.compile("^>"));
    private static final PatternMatcher percent = new PatternMatcher(Pattern.compile("^%"));
    private static final PatternMatcher equals = new PatternMatcher(Pattern.compile("^="));
    private static final PatternMatcher forwardSlash = new PatternMatcher(Pattern.compile("^/"));
    private static final PatternMatcher identifier = new PatternMatcher(Pattern.compile("^[\\p{Ll}0-9_$][\\p{L}0-9_$]*"));
    private static final PatternMatcher capitalizedIdentifier = new PatternMatcher(Pattern.compile("^\\p{Lu}[\\p{L}0-9_$]*"));
    private static final PatternMatcher dot = new PatternMatcher(Pattern.compile("^\\."));
    private static final PatternMatcher word = new PatternMatcher(Pattern.compile("^[\\w\\W&&[^\\s\\n\\r]]+"));
    private static final PatternMatcher whitespace = new PatternMatcher(Pattern.compile("^[\\s&&[^\n\r]]"));
    private static final PatternMatcher newline = new PatternMatcher(Pattern.compile("^[\\n\\r]"));

    private static final PatternMatcher dollarReference = new PatternMatcher(Pattern.compile("^(\\$)([\\w$]+(?:\\.[\\w$]+)*)"));

    private static final PatternMatcher openingComponentStart = new PatternMatcher(Pattern.compile("^<(?=\\p{Lu}|\\p{L}+(?:\\.\\p{L}+)+)"));

    private static final PatternMatcher doubleQuote = new PatternMatcher(Pattern.compile("^\""));
    private static final PatternMatcher singleQuote = new PatternMatcher(Pattern.compile("^'"));

    private static final DollarScriptletMatcher dollarScriptlet = new DollarScriptletMatcher();

    private static FunctionFsmBuilder<String, TokenizerState, FsmOutput> getFsmBuilder() {
        return new FunctionFsmBuilderImpl<>();
    }

    public FunctionFsm<String, TokenizerState, FsmOutput> getFsm(Accumulator acc) {
        return getFsmBuilder()
                .whileIn(TokenizerState.NORMAL, sc -> {
                    sc.on(dollarReference).exec(r -> {
                        acc.accumulate(DOLLAR, r.part(1));
                        acc.accumulate(GROOVY_REFERENCE, r.part(2));
                    });
                    sc.on(dollarScriptlet).exec(o -> {
                        acc.accumulate(DOLLAR, o.part(1));
                        acc.accumulate(CURLY_OPEN, o.part(2));
                        acc.accumulate(SCRIPTLET, o.part(3));
                        acc.accumulate(CURLY_CLOSE, o.part(4));
                    });
                    sc.on(openingComponentStart).shiftTo(TokenizerState.COMPONENT).exec(r -> {
                        acc.accumulate(LESS_THAN, r.entire());
                    });
                })
                .build();
    }
    
}
