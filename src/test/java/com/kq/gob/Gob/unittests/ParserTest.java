package com.kq.gob.Gob.unittests;

import com.kq.gob.Gob.*;
import com.kq.gob.Gob.Stmt;

import static com.kq.gob.Gob.TokenType.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ParserTest {

    @Test
    public void testPrint() {
        Parser parser = new Parser(List.of(
                new Token(PRINT, "daabac", null, 1),
                new Token(NUMBER, "42", 42, 1),
                new Token(SEMICOLON, ";", null, 1),
                new Token(EOF, "", null, 1)
        ));
        List<Stmt> parsed = parser.parse();
        assertTrue(parsed.get(0) instanceof Stmt.Print, "Expected a Print statement");
    }

    @Test
    public void testVariableDeclaration() {
        Parser parser = new Parser(List.of(
                new Token(VAR, "door", null, 1),
                new Token(IDENTIFIER, "x", null, 1),
                new Token(EQUAL, "=", null, 1),
                new Token(NUMBER, "5", 5, 1),
                new Token(SEMICOLON, ";", null, 1),
                new Token(EOF, "", null, 1)
        ));
        List<Stmt> parsed = parser.parse();
        assertTrue(parsed.get(0) instanceof Stmt.Var, "Expected a variable declaration statement");
        System.out.println(parsed.get(0));
        Stmt.Var varStmt = (Stmt.Var) parsed.get(0);
        assertEquals("x", varStmt.name.lexeme, "Variable name should be 'x'");
        assertEquals(5, ((Expr.Literal) varStmt.initializer).value, "Variable initializer should be 5");
    }

    @Test
    public void testArithmeticExpression() {
        Parser parser = new Parser(List.of(
                new Token(NUMBER, "3", 3, 1),
                new Token(PLUS, "+", null, 1),
                new Token(NUMBER, "4", 4, 1),
                new Token(SEMICOLON, ";", null, 1),
                new Token(EOF, "", null, 1)
        ));
        List<Stmt> parsed = parser.parse();
        assertTrue(parsed.get(0) instanceof Stmt.Expression, "Expected an expression statement");

        Stmt.Expression expressionStmt = (Stmt.Expression) parsed.get(0);
        assertTrue(expressionStmt.expression instanceof Expr.Binary, "Expression should be a binary operation");

        Expr.Binary binaryExpr = (Expr.Binary) expressionStmt.expression;
        assertEquals(3, ((Expr.Literal) binaryExpr.left).value, "Left operand should be 3");
        assertEquals(4, ((Expr.Literal) binaryExpr.right).value, "Right operand should be 4");
        assertEquals(PLUS, binaryExpr.operator.type, "Operator should be PLUS");
    }

    @Test
    public void testGroupingExpression() {
        Parser parser = new Parser(List.of(
                new Token(LEFT_PAREN, "(", null, 1),
                new Token(NUMBER, "1", 1, 1),
                new Token(PLUS, "+", null, 1),
                new Token(NUMBER, "2", 2, 1),
                new Token(RIGHT_PAREN, ")", null, 1),
                new Token(SEMICOLON, ";", null, 1),
                new Token(EOF, "", null, 1)
        ));
        List<Stmt> parsed = parser.parse();
        assertTrue(parsed.get(0) instanceof Stmt.Expression, "Expected an expression statement");

        Stmt.Expression expressionStmt = (Stmt.Expression) parsed.get(0);
        assertTrue(expressionStmt.expression instanceof Expr.Grouping, "Expression should be a grouping");

        Expr.Grouping groupingExpr = (Expr.Grouping) expressionStmt.expression;
        Expr.Binary binaryExpr = (Expr.Binary) groupingExpr.expression;
        assertEquals(1, ((Expr.Literal) binaryExpr.left).value, "Left operand should be 1");
        assertEquals(2, ((Expr.Literal) binaryExpr.right).value, "Right operand should be 2");
        assertEquals(PLUS, binaryExpr.operator.type, "Operator should be PLUS");
    }



    @Test
    public void testLogicalExpression() {
        Parser parser = new Parser(List.of(
                new Token(NUMBER, "3", 3, 1),
                new Token(GREATER, ">", null, 1),
                new Token(NUMBER, "1", 1, 1),
                new Token(SEMICOLON, ";", null, 1),
                new Token(EOF, "", null, 1)
        ));
        List<Stmt> parsed = parser.parse();
        assertTrue(parsed.get(0) instanceof Stmt.Expression, "Expected an expression statement");

        Stmt.Expression expressionStmt = (Stmt.Expression) parsed.get(0);
        assertTrue(expressionStmt.expression instanceof Expr.Binary, "Expression should be a binary operation");

        Expr.Binary binaryExpr = (Expr.Binary) expressionStmt.expression;
        assertEquals(3, ((Expr.Literal) binaryExpr.left).value, "Left operand should be 3");
        assertEquals(1, ((Expr.Literal) binaryExpr.right).value, "Right operand should be 1");
        assertEquals(GREATER, binaryExpr.operator.type, "Operator should be GREATER");
    }

    @Test
    public void testFunctionDeclaration() {
        Parser parser = new Parser(List.of(
                new Token(FUN, "fun", null, 1),
                new Token(IDENTIFIER, "add", null, 1),
                new Token(LEFT_PAREN, "(", null, 1),
                new Token(IDENTIFIER, "a", null, 1),
                new Token(COMMA, ",", null, 1),
                new Token(IDENTIFIER, "b", null, 1),
                new Token(RIGHT_PAREN, ")", null, 1),
                new Token(LEFT_BRACE, "{", null, 1),
                new Token(RETURN, "return", null, 1),
                new Token(IDENTIFIER, "a", null, 1),
                new Token(PLUS, "+", null, 1),
                new Token(IDENTIFIER, "b", null, 1),
                new Token(SEMICOLON, ";", null, 1),
                new Token(RIGHT_BRACE, "}", null, 1),
                new Token(EOF, "", null, 1)
        ));
        List<Stmt> parsed = parser.parse();
        assertTrue(parsed.get(0) instanceof Stmt.Function, "Expected a function declaration statement");

        Stmt.Function functionStmt = (Stmt.Function) parsed.get(0);
        assertEquals("add", functionStmt.name.lexeme, "Function name should be 'add'");
        assertEquals(2, functionStmt.params.size(), "Function should have two parameters");
        assertEquals("a", functionStmt.params.get(0).lexeme, "First parameter should be 'a'");
        assertEquals("b", functionStmt.params.get(1).lexeme, "Second parameter should be 'b'");
    }
}
