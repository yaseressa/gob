package com.kq.gob.Gob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kq.gob.Gob.TokenType.*;

public class Scanner {
    private static final Map<String, TokenType> keywords;
    static {
        keywords = new HashMap<>();
        keywords.put("iyo", AND);
        keywords.put("cayn", CLASS);
        keywords.put("kale", ELSE);
        keywords.put("been", FALSE);
        keywords.put("wareeg", FOR);
        keywords.put("qabte", FUN);
        keywords.put("kol", IF);
        keywords.put("kolkale", ELSE_IF);
        keywords.put("ban", NIL);
        keywords.put("ama", OR);
        keywords.put("dhaxal", EXTENDS);
        keywords.put("daabac", PRINT);
        keywords.put("daabacLn", PRINTLN);
        keywords.put("celi", RETURN);
        keywords.put("ab", SUPER);
        keywords.put("kan", THIS);
        keywords.put("run", TRUE);
        keywords.put("door", VAR);
        keywords.put("intay", WHILE);
        keywords.put("dherer", LENGTH);
    }
    private final List<Token> tokens = new ArrayList<>();
    private final String source;
    private int start = 0;
    private int current = 0;
    private int line = 1;

    public Scanner(String source) {
        this.source = source;
    }

    private char advance() {

        return source.charAt(current++);
    }
    private void addToken(TokenType type) {
        addToken(type, null);
    }
    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;
        current++;
        return true;
    }
    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }
    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }
    private void string(char s) {
        while ((s == '"' && peek() != '"') || (s == '\'' && peek() != '\'') && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }
        if (isAtEnd()) {
            Gob.error(line, "string aan dhamaystirnayn.");
            return;
        }
        advance();
        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }
    private void number() {
        while (isDigit(peek())) advance();
        if (peek() == '.' && isDigit(peekNext())) {
            advance();
            while (isDigit(peek())) advance();
        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
        return;
        }
        addToken(NUMBER, Integer.parseInt(source.substring(start, current)));
    }
    private boolean isAlphaNumeric(char c) { return isAlpha(c) || isDigit(c);}
    private void identifier() {
        while (isAlphaNumeric(peek())) advance();
        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null) type = IDENTIFIER;
        addToken(type);
    }
    private boolean isAlpha(char c) {return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';}
    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(':
                addToken(TokenType.LEFT_PAREN);
                break;
            case '%':
                addToken(match('=') ? COMPOUND_PERCENT: PERCENT);
                break;
            case ')':
                addToken(TokenType.RIGHT_PAREN);
                break;
            case '{':
                addToken(TokenType.LEFT_BRACE);
                break;
            case '}':
                addToken(TokenType.RIGHT_BRACE);
                break;
            case '[':
                addToken(LEFT_SQUARE);
                break;
            case ']':
                addToken(RIGHT_SQUARE);
                break;
            case ',':
                addToken(TokenType.COMMA);
                break;
            case '.':
                addToken(TokenType.DOT);
                break;
            case '-':
                addToken(match('=') ? COMPOUND_MINUS : MINUS);
                break;
            case '+':
                addToken(match('=') ? COMPOUND_PLUS: PLUS);
                break;
            case ';':
                addToken(TokenType.SEMICOLON);
                break;
            case '*':
                addToken(match('=') ? COMPOUND_STAR : STAR);
                break;
            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER);
                break;
            case '/':
                if (match('/')) while (peek() != '\n' && !isAtEnd()) advance();
                else addToken(match('=') ? COMPOUND_SLASH: SLASH);
                break;
            case ' ': case '\r': case '\t': break; case '\n':
                line++;
                break;
            case '"':
                string('"');
                break;
            case '\'':
                string('\'');
                break;
            default:
                if (isDigit(c)) {
                    number();
                }else if (isAlpha(c)) {
                    identifier();
                } else {
                    Gob.error(line, "Xaraf lama filaan ah.");
                }
                break;
        }
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {start = current; scanToken();}
        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }
    private boolean isAtEnd() {
        return current >= source.length();
    }
}