package com.kq.gob.Gob;

import static com.kq.gob.Gob.TokenType.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parser {
    private static class ParseError extends RuntimeException {}
    private final List<Token> tokens;
    private int current;
    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }
    public List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(declaration());
        }
        return statements;
    }
    private Expr expression() {
        return assignment();
    }

    private Expr assignment() {

        Expr expr = or();
        if (match(EQUAL)) {
            Token equals = previous();
            Expr value = assignment();
            if (expr instanceof Expr.Variable) {
                Token name = ((Expr.Variable) expr).name;
                return new Expr.Assign(name, value);
            }else if (expr instanceof Expr.Get) {
                Expr.Get get = (Expr.Get)expr;
                return new Expr.Set(get.object, get.name, value);
            }
            else if (expr instanceof Expr.ListCall) {
                Expr.ListCall get = (Expr.ListCall)expr;
                return new Expr.ListUpdate(get.callee, get.index, value, get.bracket);
            }
            else {
                error(equals, "Meelayn Aan La Oogalyn");
            }
        }
        return expr;
    }

    private Expr or() {
        Expr expr = and();
        if (match(OR)) {
            Token op = previous();
            Expr right = and();
            expr = new Expr.Logical(expr, op, right);

        }
        return expr;
    }

    private Expr and() {
        Expr expr = equality();
        if (match(AND)) {
            Token op = previous();
            Expr right = equality();
            expr = new Expr.Logical(expr, op, right);

        }
        return expr;
    }

    private Stmt declaration() {
        try {
            if (match(VAR)) return varDeclaration();
            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    private Stmt varDeclaration() {
        Token name = consume(IDENTIFIER, "La Filayey magaca doorsome");
        Object initializer = null;
        if (match(EQUAL)) {

            initializer = expression();
        }
        consume(SEMICOLON, "La Filayey ';' Magacabista doorsome Kadib.");
        return new Stmt.Var(name, (Expr) initializer);
    }

    private void synchronize(){
        advance();

        if(!isAtEnd()){
            if(previous().type == SEMICOLON) return;
            switch (peek().type) {
                case CLASS, FUN, VAR, FOR, IF, WHILE, PRINT, RETURN -> {
                    return;
                }
            }
        }
        advance();
    }
    private ParseError error(Token token, String message) {
        Gob.error(token, message);
        return new ParseError();
    }
    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();
        throw error(peek(), message);
    }
    private List<Stmt> block() {
        List<Stmt> statements = new ArrayList<>();
        while (!check(RIGHT_BRACE) && !isAtEnd()) {
            statements.add(declaration());
        }
        consume(RIGHT_BRACE, "La Filayey '}' sakad Kadib.");
        return statements;
    }
    private Stmt printStatement() {
        Expr value ;
        if (match(LEFT_PAREN)) {
            value = expression();
            consume(RIGHT_PAREN, "La Filayey ')' tacbiir Kadib");
        } else {
            value = expression();
        }
        consume(SEMICOLON, "La Filayey ';' Qiimo Kadib.");
        return new Stmt.Print(value);
    }
    private Stmt printLNStatement() {
        Expr value ;
        if (match(LEFT_PAREN)) {
            value = expression();
            consume(RIGHT_PAREN, "La Filayey ')' tacbiir Kadib");
        } else {
            value = expression();
        }
        consume(SEMICOLON, "La Filayey ';' Qiimo Kadib.");
        return new Stmt.PrintLN(value);
    }
    private Expr lengthStatement() {
        Expr value ;
        if (match(LEFT_PAREN)) {
            value = expression();
            consume(RIGHT_PAREN, "La Filayey ')' tacbiir Kadib");
        } else {
            value = expression();
        }
        return new Expr.Length(value);
    }
    private Stmt expressionStatement() {
        Expr expr = expression();
        consume(SEMICOLON, "La Filayey ';' Qiimo Kadib.");
        return new Stmt.Expression(expr);
    }
    private Stmt ifStatement() {
        var left = consume(LEFT_PAREN, "La Filayey '(' kol kadib.  ");
        var con = expression();
        var right = consume(RIGHT_PAREN, "La Filayey ')' Tacbiir Kadib. ");
        Stmt thenBranch = statement(), elseBranch = null, elseIf = null;
        if (match(ELSE)) {
            elseBranch = statement();
        }
        while (match(ELSE_IF)) {
            elseIf = ifStatement();
        }

        return new Stmt.If(con, thenBranch, elseBranch, elseIf);
    }
    private Stmt whileStatement() {
        consume(LEFT_PAREN, "La Filayey '(' Intay Kadib. ");
        Expr condition = expression();
        consume(RIGHT_PAREN, "La Filayey ')' Tacbiir Kadib. ");
        Stmt body = statement();
        return new Stmt.While(condition, body);
    }

    private Stmt forStatement() {
        consume(LEFT_PAREN, "La Filayey '(' intay Kadib. ");
        Stmt initializer;
        if (match(SEMICOLON)) {
            initializer = null;
        } else if (match(VAR)) {
            initializer = varDeclaration();
        } else {
            initializer = expressionStatement();
        }
        Expr condition = null;
        if (!check(SEMICOLON)) {
            condition = expression();
        }
        consume(SEMICOLON, "Expect ';' after loop condition.");
        Expr increment = null;
        if (!check(RIGHT_PAREN)) {
            increment = expression();
        }
        consume(RIGHT_PAREN, "Expect ')' after for clauses.");
        Stmt body = statement();
        if (increment != null) {
            body = new Stmt.Block(
                    Arrays.asList(body, new Stmt.Expression(increment)));
        }
        if (condition == null) condition = new Expr.Literal("run");
        body = new Stmt.While(condition, body);
        if (initializer != null) {
            body = new Stmt.Block(Arrays.asList(initializer, body));
        }
        return body;
    }
    private Stmt.Function function(String kind) {
        Token name = consume(IDENTIFIER, "La Filayey " + kind + " Magacii.");
        consume(LEFT_PAREN, "La Filayey '(' magaca " + kind + " kadib.");
        List<Token> parameters = new ArrayList<>();
        if (!check(RIGHT_PAREN)) {
            do {
                parameters.add(
                        consume(IDENTIFIER, "La Filayey magaca halbeeg"));
            } while (match(COMMA));
        }
        consume(RIGHT_PAREN, "La Filayey ')' halbeeg Kadib");
        consume(LEFT_BRACE, "La Filayey '{' Kahor jidhka " + kind);
        List<Stmt> body = block();
        return new Stmt.Function(name, parameters, body);
    }
    private Object method(String kind) {
        Token name = consume(IDENTIFIER, "La Filayey " + kind + " Magacii.");
        if(check(LEFT_PAREN)){
            consume(LEFT_PAREN, "La Filayey '(' magaca " + kind + " kadib.");
            List<Token> parameters = new ArrayList<>();
            if (!check(RIGHT_PAREN)) {
                do {
                    parameters.add(
                            consume(IDENTIFIER, "La Filayey magaca halbeeg"));
                } while (match(COMMA));
            }
            consume(RIGHT_PAREN, "La Filayey ')' halbeeg Kadib");
            consume(LEFT_BRACE, "La Filayey '{' Kahor jidhka " + kind);
            List<Stmt> body = block();
            return new Stmt.Function(name, parameters, body);
        }else{
            Expr initializer = null;
            if (match(EQUAL)) {
                initializer = expression();
            }
            consume(SEMICOLON, "La Filayey ';' Magacabista doorsome Kadib.");
            return new Stmt.Var(name, initializer);
        }
    }
    private Stmt classDeclaration() {
        Token name = consume(IDENTIFIER, "La Filayey cayn Magacii.");
        Expr.Variable superclass = null;
        if (match(EXTENDS)) {
            consume(IDENTIFIER, "La Filayey Magac Waalid.");
            superclass = new Expr.Variable(previous());
        }
        consume(LEFT_BRACE, "La Filayey '(' magaca cayn kadib.");
        List<Object> methods = new ArrayList<>();
        while (!check(RIGHT_BRACE) && !isAtEnd()) {
            methods.add(method("method"));

        }
        consume(RIGHT_BRACE, "La Filayey '}' cayn dhamaadkii");
        return new Stmt.Class(name, superclass, methods);
    }

    private Stmt returnStatement() {
        Token tok = previous();
        Expr e = null;
        if(!(check(SEMICOLON)))
            if(match(LEFT_PAREN)) {
                e = expression();
                consume(RIGHT_PAREN, "La Filayey ')' tacbiir Kadib");
            }else{
                e = expression();
            }
        consume(SEMICOLON, "La Filayey ';' tacbiir Kadib");
        return new Stmt.Return(tok,e);
    }
    private Expr finishCall(Expr callee) {
        List<Expr> arguments = new ArrayList<>();
        if (!check(RIGHT_PAREN)) {
            do {
                arguments.add(expression());
            } while (match(COMMA));
        }
        Token paren = consume(RIGHT_PAREN,
                "La Filayey ')' masalo Kadib");
        return new Expr.Call(callee, paren, arguments);
    }
    private Stmt statement() {
        if(match(IF)) return ifStatement();
        if(match(FOR)) return forStatement();
        if(match(WHILE)) return whileStatement();
        if (match(PRINT)) return printStatement();
        if (match(PRINTLN)) return printLNStatement();
        if (match(LEFT_BRACE)) return new Stmt.Block(block());
        if (match(FUN)) return function("qabte");
        if (match(RETURN)) return returnStatement();
        if (match(CLASS)) return classDeclaration();
        return expressionStatement();
    }
    private Expr primary(){
        if(match(FALSE)) return new Expr.Literal("been");
        if (match(THIS)) return new Expr.This(previous());
        if (match(SUPER)) {
            Token keyword = previous();
            consume(DOT, "La Filayey '.'  'ab' Kadib.");
            Token method = consume(IDENTIFIER,
                    "La Filayey qabte Uu Waalid Leyahay.");
            return new Expr.Super(keyword, method);
        }
        if(match(TRUE)) return new Expr.Literal("run");
        if(match(NIL)) return new Expr.Literal("ban");
        if(match(LENGTH)) return lengthStatement();
        if(match(LEFT_SQUARE)) {
            ArrayList<Expr> elements = new ArrayList<>();
            if(!check(RIGHT_SQUARE)) {
                elements.add(expression());
                while(match(COMMA)) {
                    elements.add(expression());
                }
            }
            consume(RIGHT_SQUARE, "La Filayey ']' god Kadib");
            return new Expr.ListLiteral(elements);
        }
        if (match(IDENTIFIER)) {
            var variable = previous();
            return new Expr.Variable(variable);
        }
        if(match(NUMBER, STRING)){
            return new Expr.Literal(previous().literal);
        }
        if(match(LEFT_BRACE)){
            Expr expr = expression();
            consume(RIGHT_PAREN, "La Filayey ')' Tacbiir Kadib.");
            return new Expr.Grouping(expr);
        }
        throw error(peek(), "La Filayey Tacbiir ");
    }
    private Expr call() {
        Expr expr = primary();
        while (true) {
            if (match(LEFT_PAREN)) {
                expr = finishCall(expr);
            } else if (match(LEFT_SQUARE)) {
                Token bracket = previous();
                Expr index = expression();
                consume(RIGHT_SQUARE, "La Filayey ']' god Kadib");
                expr = new Expr.ListCall(expr, index, bracket);
            } else if (match(DOT)) {
                Token name = consume(IDENTIFIER, "La Filayay Magac sifo Kadib '.'.");
                expr = new Expr.Get(expr, name);
            } else {
                break;
            }
        }
        return expr;
    }
    private Expr unary() {
        if (match(BANG, MINUS)) {
            Token operator = previous();
            return new Expr.Unary(operator, unary());
        }
        return call();

    }
    private Expr factor() {
        Expr expr = parenthesis();
        while (match(SLASH, STAR, PERCENT)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }
        while (match(COMPOUND_SLASH, COMPOUND_STAR, COMPOUND_PERCENT)) {
            Token operator = previous();
            Expr right = factor();
            if (expr instanceof Expr.Variable) {
                Token name = ((Expr.Variable) expr).name;
                expr = new Expr.CompAssign(name, operator, right);
            }
        }
        return expr;
    }
    private Expr parenthesis() {
        if (match(LEFT_PAREN)) {
            Expr expr = expression();
            consume(RIGHT_PAREN, "La Filayey ')' Tacbiir Kadib.");
            return new Expr.Grouping(expr);
        }
        return unary();
    }
    private Expr term() {
        Expr expr = factor();
        while (match(MINUS, PLUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);

        }
        while (match(COMPOUND_MINUS, COMPOUND_PLUS)) {
            Token operator = previous();
            Expr right = factor();
            if (expr instanceof Expr.Variable) {
                Token name = ((Expr.Variable) expr).name;
                expr = new Expr.CompAssign(name, operator, right);
            }
        }
        return expr;
    }
    private Expr comparison() {
        Expr expr = term();
        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }
    private Expr equality() {
        Expr expr = comparison();
        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }
    private Token peek(){
        return tokens.get(current);
    }
    private boolean isAtEnd(){
        if(peek().type == EOF) return true;
        return false;
    }
    private boolean check(TokenType t){
        if(isAtEnd()) return false;
        return t == peek().type;
    }
    private Token previous(){
        return tokens.get(current - 1);
    }
    private Token advance(){
        if(!isAtEnd()) current++;
        return previous();
    }
    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }
}
