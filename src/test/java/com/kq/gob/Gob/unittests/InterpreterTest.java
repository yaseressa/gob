package com.kq.gob.Gob.unittests;

import com.kq.gob.Gob.Interpreter;
import com.kq.gob.Gob.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class InterpreterTest {
    private final Interpreter interpreter = new Interpreter();

    @Test
    void testArithmeticOperations() {
        // Test simple arithmetic
        Stmt.Print print = new Stmt.Print(
                new Expr.Binary(
                        new Expr.Literal(5),
                        new Token(TokenType.PLUS, "+", null, 1),
                        new Expr.Literal(3)
                )
        );
        assertEquals("8", interpreter.interpret(List.of(print)).toString().trim());
    }

    @Test
    void testVariableAssignmentAndRetrieval() {
        // Assign a variable and retrieve its value
        Stmt.Var varStmt = new Stmt.Var(
                new Token(TokenType.IDENTIFIER, "x", null, 1),
                new Expr.Literal(10)
        );
        interpreter.interpret(List.of(varStmt));

        Expr.Variable varExpr = new Expr.Variable(
                new Token(TokenType.IDENTIFIER, "x", null, 1)
        );
        assertEquals(10, interpreter.evaluate(varExpr));
    }

    @Test
    void testLogicalOperations() {
        // Test logical AND
        Stmt.Print print = new Stmt.Print(
                new Expr.Logical(
                        new Expr.Literal("run"),
                        new Token(TokenType.AND, "iyo", null, 1),
                        new Expr.Literal("been")
                )
        );
        assertTrue("been".equals(interpreter.interpret(List.of(print)).toString().trim()));
    }

    @Test
    void testConditionalStatements() {
        // Test if-else statement
        Stmt.If ifStmt = new Stmt.If(
                new Expr.Literal("been"), // condition
                new Stmt.Print(new Expr.Literal("Then branch")),
                new Stmt.Print(new Expr.Literal("Else branch")),
                null
        );
        assertTrue("Else branch".equals(interpreter.interpret(List.of(ifStmt)).toString().trim()));
    }

    @Test
    void testFunctionCall() {
        // Define and call a function
        Stmt.Function funcStmt = new Stmt.Function(
                new Token(TokenType.IDENTIFIER, "greet", null, 1),
                List.of(),
                List.of(
                        new Stmt.Return(new Token(TokenType.RETURN, "celi", null, 1), new Expr.Literal("Hello, World!"))
                )
        );

        Stmt.Print greet = new Stmt.Print(new Expr.Call(
                new Expr.Variable(new Token(TokenType.IDENTIFIER, "greet", null, 1)),
                new Token(TokenType.RIGHT_PAREN, ")", null, 1),
                List.of()
        ));

        assertTrue("Hello, World!".equals(interpreter.interpret(List.of(funcStmt, greet)).toString().trim()));
    }

    @Test
    void testListOperations() {
        // Test list creation and retrieval
        Stmt.Listing listStmt = new Stmt.Listing(
                new Token(TokenType.IDENTIFIER, "numbers", null, 1),
                new ArrayList<Expr>(List.of(
                        new Expr.Literal(1),
                        new Expr.Literal(2),
                        new Expr.Literal(3)
                ))
        );
        interpreter.interpret(List.of(listStmt));

        Expr.ListCall listCall = new Expr.ListCall(
                new Expr.Variable(new Token(TokenType.IDENTIFIER, "numbers", null, 1)),
                new Expr.Literal(1),
                new Token(TokenType.LEFT_SQUARE, "[", null, 1)
        );
        assertEquals(2, interpreter.evaluate(listCall));
    }

    @Test
    void testClassInstantiationAndMethods() {
        // Define a class and instantiate it
        Stmt.Class classStmt = new Stmt.Class(
                new Token(TokenType.IDENTIFIER, "Dog", null, 1),
                null,
                List.of(
                        new Stmt.Function(
                                new Token(TokenType.IDENTIFIER, "bark", null, 1),
                                List.of(),
                                List.of(
                                        new Stmt.Print(new Expr.Literal("Woof!"))
                                )
                        )
                )
        );
        Stmt.Var objStmt = new Stmt.Var(
                new Token(TokenType.IDENTIFIER, "dog", null, 1),
                new Expr.Call(
                        new Expr.Variable(new Token(TokenType.IDENTIFIER, "Dog", null, 1)),
                        new Token(TokenType.RIGHT_PAREN, ")", null, 1),
                        List.of()
                )
        );

        Stmt.Expression expression = new Stmt.Expression(new Expr.Call(
                new Expr.Get(
                        new Expr.Variable(new Token(TokenType.IDENTIFIER, "dog", null, 1)),
                        new Token(TokenType.IDENTIFIER, "bark", null, 1)
                ),
                new Token(TokenType.RIGHT_PAREN, ")", null, 1),
                List.of()
        ));
        String trimmed = interpreter.interpret(List.of(classStmt, objStmt, expression)).toString().trim();

        assertEquals("Woof!".equals(trimmed), true);
    }
}
