package com.jessebrault.gcp.tokenizer;

import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Tokenizer {

    private static final Pattern lessThan = Pattern.compile("^<");
    private static final Pattern greaterThan = Pattern.compile("^>");
    private static final Pattern percent = Pattern.compile("^%");
    private static final Pattern equals = Pattern.compile("^=");
    private static final Pattern doubleQuote = Pattern.compile("^\"");
    private static final Pattern singleQuote = Pattern.compile("^'");
    private static final Pattern forwardSlash = Pattern.compile("^/");
    private static final Pattern identifier = Pattern.compile("^[\\p{Ll}0-9_$][\\p{L}0-9_$]*");
    private static final Pattern capitalizedIdentifier = Pattern.compile("^\\p{Lu}[\\p{L}0-9_$]*");
    private static final Pattern dot = Pattern.compile("^\\.");
    private static final Pattern word = Pattern.compile("^[\\w\\W&&[^\\s\\n\\r]]+");
    private static final Pattern whitespace = Pattern.compile("^[\\s&&[^\n\r]]");
    private static final Pattern newline = Pattern.compile("^[\\n\\r]");

    public static Queue<Token> tokenize(final String gcpSrc) {
        Queue<Token> tokens = new LinkedList<>();

        var line = 1;
        var col = 1;
        
        String remaining = gcpSrc;
        while (remaining.length() > 0) {
            Matcher m;
            
            if ((m = lessThan.matcher(remaining)).find()) {
                tokens.add(new Token(Token.Type.LESS_THAN, m.group(0), line, col));
                col += m.group(0).length();
            } else if ((m = greaterThan.matcher(remaining)).find()) {
                tokens.add(new Token(Token.Type.GREATER_THAN, m.group(0), line, col));
                col += m.group(0).length();
            } else if ((m = percent.matcher(remaining)).find()) {
                tokens.add(new Token(Token.Type.PERCENT, m.group(0), line, col));
                col += m.group(0).length();
            } else if ((m = equals.matcher(remaining)).find()) {
                tokens.add(new Token(Token.Type.EQUALS, m.group(0), line, col));
                col += m.group(0).length();
            } else if ((m = doubleQuote.matcher(remaining)).find()) {
                tokens.add(new Token(Token.Type.DOUBLE_QUOTE, m.group(0), line, col));
                col += m.group(0).length();
            } else if ((m = singleQuote.matcher(remaining)).find()) {
                tokens.add(new Token(Token.Type.SINGLE_QUOTE, m.group(0), line, col));
                col += m.group(0).length();
            } else if ((m = identifier.matcher(remaining)).find()) {
                tokens.add(new Token(Token.Type.IDENTIFIER, m.group(0), line, col));
                col += m.group(0).length();
            } else if ((m = forwardSlash.matcher(remaining)).find()) {
                tokens.add(new Token(Token.Type.FORWARD_SLASH, m.group(0), line, col));
                col += m.group(0).length();
            } else if ((m = capitalizedIdentifier.matcher(remaining)).find()) {
                tokens.add(new Token(Token.Type.CAPITALIZED_IDENTIFIER, m.group(0), line, col));
                col += m.group(0).length();
            } else if ((m = dot.matcher(remaining)).find()) {
                tokens.add(new Token(Token.Type.DOT, m.group(0), line, col));
                col += m.group(0).length();
            } else if ((m = word.matcher(remaining)).find()) {
                tokens.add(new Token(Token.Type.WORD, m.group(0), line, col));
                col += m.group(0).length();
            } else if ((m = whitespace.matcher(remaining)).find()) {
                tokens.add(new Token(Token.Type.WHITESPACE, m.group(0), line, col));
                col += m.group(0).length();
            } else if ((m = newline.matcher(remaining)).find()) {
                tokens.add(new Token(Token.Type.NEWLINE, m.group(0), line, col));
                col = 1;
                line++;
            }

            remaining = remaining.substring(m.group(0).length());
        }

        return tokens;
    }

}
