package com.jessebrault.gcp.tokenizer;

import com.jessebrault.fsm.function.FunctionFsm;
import com.jessebrault.fsm.function.FunctionFsmBuilder;
import com.jessebrault.fsm.function.FunctionFsmBuilderImpl;

import static com.jessebrault.gcp.tokenizer.Token.Type.*;

import java.util.regex.Pattern;

final class TokenizerFsm {

    /**
     * Text
     */
    private static final PatternMatcher text = new PatternMatcher(
            Pattern.compile("^(?:[\\w\\W&&[^<$]]|<(?!%|/?\\p{Lu}|/?[\\p{L}0-9_$]+(?:\\.[\\p{L}0-9_$]+)+)|\\$(?![\\w$]+(?:\\.[\\w$]+)*))+")
    );

    /**
     * Gsp dollar reference and scriptlets, also used as component values
     */
    private static final PatternMatcher dollarReference = new PatternMatcher(
            Pattern.compile("^(\\$)([\\w$]+(?:\\.[\\w$]+)*)")
    );
    private static final DollarScriptletMatcher dollarScriptlet = new DollarScriptletMatcher();
    private static final PatternMatcher blockScriptlet = new PatternMatcher(
            Pattern.compile("^(<%)(.*?)(%>)")
    );
    private static final PatternMatcher expressionScriptlet = new PatternMatcher(
            Pattern.compile("^(<%=)(.*?)(%>)")
    );

    /**
     * Component starts
     */
    private static final PatternMatcher openingComponentStart = new PatternMatcher(
            Pattern.compile("^<(?=\\p{Lu}|[\\p{L}0-9_$]+(?:\\.[\\p{L}0-9_$]+)+)")
    );
    private static final PatternMatcher closingComponentStart = new PatternMatcher(
            Pattern.compile("^(<)(/)(?=\\p{Lu}|[\\p{L}0-9_$]+(?:\\.[\\p{L}0-9_$]+)+)")
    );

    /**
     * Component names
     */
    private static final PatternMatcher className = new PatternMatcher(
            Pattern.compile("^\\p{Lu}[\\p{L}0-9_$]*")
    );
    private static final PatternMatcher packageName = new PatternMatcher(
            Pattern.compile("^[\\p{L}0-9_$]+(?=\\.)")
    );
    private static final PatternMatcher dot = new PatternMatcher(
            Pattern.compile("^\\.")
    );

    /**
     * Whitespace
     */
    private static final PatternMatcher whitespace = new PatternMatcher(Pattern.compile("^\\s+"));

    /**
     * Keys and values
     */
    private static final PatternMatcher key = new PatternMatcher(
            Pattern.compile("^[\\p{L}0-9_$]+")
    );
    private static final PatternMatcher equals = new PatternMatcher(Pattern.compile("^="));
    private static final PatternMatcher singleQuoteString = new PatternMatcher(
            Pattern.compile("^(')((?:[\\w\\W&&[^\\\\'\\n\\r]]|\\\\['nrbfst\\\\u])*)(')")
    );
    private static final GStringMatcher gString = new GStringMatcher();

    /**
     * Component ends
     */
    private static final PatternMatcher forwardSlash = new PatternMatcher(Pattern.compile("^/"));
    private static final PatternMatcher componentEnd = new PatternMatcher(Pattern.compile("^>"));

    private static FunctionFsmBuilder<String, TokenizerState, FsmOutput> getFsmBuilder() {
        return new FunctionFsmBuilderImpl<>();
    }

    public static FunctionFsm<String, TokenizerState, FsmOutput> get(Accumulator acc) {
        return getFsmBuilder()
                .setInitialState(TokenizerState.NORMAL)
                .whileIn(TokenizerState.NORMAL, sc -> {
                    sc.on(text).exec(o -> {
                        acc.accumulate(TEXT, o.entire());
                    });
                    sc.on(dollarReference).exec(o -> {
                        acc.accumulate(DOLLAR, o.part(1));
                        acc.accumulate(GROOVY_REFERENCE, o.part(2));
                    });
                    sc.on(dollarScriptlet).exec(o -> {
                        acc.accumulate(DOLLAR, o.part(1));
                        acc.accumulate(CURLY_OPEN, o.part(2));
                        acc.accumulate(SCRIPTLET, o.part(3));
                        acc.accumulate(CURLY_CLOSE, o.part(4));
                    });
                    sc.on(blockScriptlet).exec(o -> {
                        acc.accumulate(BLOCK_SCRIPTLET_OPEN, o.part(1));
                        acc.accumulate(SCRIPTLET, o.part(2));
                        acc.accumulate(SCRIPTLET_CLOSE, o.part(3));
                    });
                    sc.on(expressionScriptlet).exec(o -> {
                        acc.accumulate(EXPRESSION_SCRIPTLET_OPEN, o.part(1));
                        acc.accumulate(SCRIPTLET, o.part(2));
                        acc.accumulate(SCRIPTLET_CLOSE, o.part(3));
                    });
                    sc.on(openingComponentStart).shiftTo(TokenizerState.COMPONENT_NAME).exec(o ->
                        acc.accumulate(COMPONENT_START, o.entire())
                    );
                    sc.on(closingComponentStart).shiftTo(TokenizerState.COMPONENT_NAME).exec(o -> {
                        acc.accumulate(COMPONENT_START, o.part(1));
                        acc.accumulate(FORWARD_SLASH, o.part(2));
                    });
                    sc.onNoMatch().exec(input -> { throw new IllegalArgumentException(); });
                })
                .whileIn(TokenizerState.COMPONENT_NAME, sc -> {
                    sc.on(packageName).exec(o -> {
                       acc.accumulate(PACKAGE_NAME, o.entire());
                    });
                    sc.on(dot).exec(o -> {
                        acc.accumulate(DOT, o.entire());
                    });
                    sc.on(className).exec(o -> {
                        acc.accumulate(CLASS_NAME, o.entire());
                    });
                    sc.on(forwardSlash).exec(o -> {
                        acc.accumulate(FORWARD_SLASH, o.entire());
                    });
                    sc.on(componentEnd).shiftTo(TokenizerState.NORMAL).exec(o -> {
                       acc.accumulate(COMPONENT_END, o.entire());
                    });
                    sc.on(whitespace).shiftTo(TokenizerState.COMPONENT_KEYS_AND_VALUES).exec(o -> {
                        acc.accumulate(WHITESPACE, o.entire());
                    });
                    sc.onNoMatch().exec(input -> { throw new IllegalArgumentException(); });
                })
                .whileIn(TokenizerState.COMPONENT_KEYS_AND_VALUES, sc -> {
                    sc.on(componentEnd).shiftTo(TokenizerState.NORMAL).exec(o -> {
                        acc.accumulate(COMPONENT_END, o.entire());
                    });
                    sc.on(whitespace).exec(o -> {
                        acc.accumulate(WHITESPACE, o.entire());
                    });
                    sc.on(key).exec(o -> {
                        acc.accumulate(KEY, o.entire());
                    });
                    sc.on(equals).exec(o -> {
                       acc.accumulate(EQUALS, o.entire());
                    });
                    sc.on(gString).exec(o -> {
                        acc.accumulate(DOUBLE_QUOTE, o.part(1));
                        acc.accumulate(STRING, o.part(2));
                        acc.accumulate(DOUBLE_QUOTE, o.part(3));
                    });
                    sc.on(singleQuoteString).exec(o -> {
                       acc.accumulate(SINGLE_QUOTE, o.part(1));
                       acc.accumulate(STRING, o.part(2));
                       acc.accumulate(SINGLE_QUOTE, o.part(3));
                    });
                    sc.on(dollarReference).exec(o -> {
                        acc.accumulate(DOLLAR, o.part(1));
                        acc.accumulate(GROOVY_REFERENCE, o.part(2));
                    });
                    sc.on(dollarScriptlet).exec(o -> {
                        acc.accumulate(DOLLAR, o.part(1));
                        acc.accumulate(CURLY_OPEN, o.part(2));
                        acc.accumulate(SCRIPTLET, o.part(3));
                        acc.accumulate(CURLY_CLOSE, o.part(4));
                    });
                    sc.on(blockScriptlet).exec(o -> {
                        acc.accumulate(BLOCK_SCRIPTLET_OPEN, o.part(1));
                        acc.accumulate(SCRIPTLET, o.part(2));
                        acc.accumulate(SCRIPTLET_CLOSE, o.part(3));
                    });
                    sc.on(expressionScriptlet).exec(o -> {
                        acc.accumulate(EXPRESSION_SCRIPTLET_OPEN, o.part(1));
                        acc.accumulate(SCRIPTLET, o.part(2));
                        acc.accumulate(SCRIPTLET_CLOSE, o.part(3));
                    });
                    sc.on(forwardSlash).exec(o -> {
                        acc.accumulate(FORWARD_SLASH, o.entire());
                    });
                    sc.on(componentEnd).shiftTo(TokenizerState.NORMAL).exec(o -> {
                        acc.accumulate(COMPONENT_END, o.entire());
                    });
                    sc.onNoMatch().exec(input -> { throw new IllegalArgumentException(); });
                })
                .build();
    }


    
}
