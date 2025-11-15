package com.kq.gob.Gob.unittests;

import com.kq.gob.Gob.Scanner;
import com.kq.gob.Gob.Token;
import static com.kq.gob.Gob.TokenType.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ScannerTest {

    @Test
    public void testScanTokens_emptyInput() {
        Scanner scanner = new Scanner("");
        List<Token> tokens = scanner.scanTokens();
        assertEquals(1, tokens.size(), "Expected one token for empty input (EOF token)");
        assertEquals(EOF, tokens.get(0).type, "The token type should be EOF");
    }

    @Test
    public void testScanTokens_singleNumber() {
        Scanner scanner = new Scanner("42");
        List<Token> tokens = scanner.scanTokens();
        assertEquals(2, tokens.size(), "Expected two tokens for input '42' (NUMBER and EOF)");
        assertEquals(NUMBER, tokens.get(0).type, "The token type should be NUMBER");
        assertEquals("42", tokens.get(0).lexeme, "The lexeme should be '42'");
    }

    @Test
    public void testScanTokens_arithmeticExpression() {
        Scanner scanner = new Scanner("3 + 4");
        List<Token> tokens = scanner.scanTokens();
        assertEquals(4, tokens.size(), "Expected four tokens for input '3 + 4' (NUMBER, PLUS, NUMBER, EOF)");
        assertEquals(NUMBER, tokens.get(0).type);
        assertEquals(PLUS, tokens.get(1).type);
        assertEquals(NUMBER, tokens.get(2).type);
        assertEquals(EOF, tokens.get(3).type);
    }

    @Test
    public void testScanTokens_parentheses() {
        Scanner scanner = new Scanner("(3 + 4)");
        List<Token> tokens = scanner.scanTokens();
        assertEquals(6, tokens.size(), "Expected six tokens for input '(3 + 4)'");
        assertEquals(LEFT_PAREN, tokens.get(0).type);
        assertEquals(NUMBER, tokens.get(1).type);
        assertEquals(PLUS, tokens.get(2).type);
        assertEquals(NUMBER, tokens.get(3).type);
        assertEquals(RIGHT_PAREN, tokens.get(4).type);
        assertEquals(EOF, tokens.get(5).type);
    }

    @Test
    public void testScanTokens_stringLiteral() {
        Scanner scanner = new Scanner("\"Hello, world!\"");
        List<Token> tokens = scanner.scanTokens();
        assertEquals(2, tokens.size(), "Expected two tokens for string literal input");
        assertEquals(STRING, tokens.get(0).type);
        assertEquals("Hello, world!", tokens.get(0).literal, "The literal value should be 'Hello, world!'");
        assertEquals(EOF, tokens.get(1).type);
    }

    @Test
    public void testScanTokens_keywordsAndIdentifiers() {
        Scanner scanner = new Scanner("door x = 10;");
        List<Token> tokens = scanner.scanTokens();
        assertEquals(6, tokens.size(), "Expected six tokens for input 'door x = 10;'");
        assertEquals(VAR, tokens.get(0).type);
        assertEquals(IDENTIFIER, tokens.get(1).type);
        assertEquals(EQUAL, tokens.get(2).type);
        assertEquals(NUMBER, tokens.get(3).type);
        assertEquals(SEMICOLON, tokens.get(4).type);
        assertEquals(EOF, tokens.get(5).type);
    }

    @Test
    public void testScanTokens_comments() {
        Scanner scanner = new Scanner("42 // this is a comment");
        List<Token> tokens = scanner.scanTokens();
        assertEquals(2, tokens.size(), "Expected two tokens, ignoring comment");
        assertEquals(NUMBER, tokens.get(0).type);
        assertEquals(EOF, tokens.get(1).type);
    }

    @Test
    public void testScanTokens_multiline() {
        Scanner scanner = new Scanner("door x = 10;\ndoor y = 20;");
        List<Token> tokens = scanner.scanTokens();
        assertEquals(11, tokens.size(), "Expected eleven tokens for multiline input");
        assertEquals(VAR, tokens.get(0).type);
        assertEquals(IDENTIFIER, tokens.get(1).type);
        assertEquals(EQUAL, tokens.get(2).type);
        assertEquals(NUMBER, tokens.get(3).type);
        assertEquals(SEMICOLON, tokens.get(4).type);
        assertEquals(VAR, tokens.get(5).type);
        assertEquals(IDENTIFIER, tokens.get(6).type);
        assertEquals(EQUAL, tokens.get(7).type);
        assertEquals(NUMBER, tokens.get(8).type);
        assertEquals(SEMICOLON, tokens.get(9).type);
        assertEquals(EOF, tokens.get(10).type);
    }

    @Test
    public void testScanTokens_operators() {
        Scanner scanner = new Scanner("x >= 5 <= 10");
        List<Token> tokens = scanner.scanTokens();
        assertEquals(6, tokens.size(), "Expected five tokens for input 'x >= 5 <= 10'");
        assertEquals(IDENTIFIER, tokens.get(0).type);
        assertEquals(GREATER_EQUAL, tokens.get(1).type);
        assertEquals(NUMBER, tokens.get(2).type);
        assertEquals(LESS_EQUAL, tokens.get(3).type);
        assertEquals(NUMBER, tokens.get(4).type);
        assertEquals(EOF, tokens.get(5).type);
    }


}
