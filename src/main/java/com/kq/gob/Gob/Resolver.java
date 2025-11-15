package com.kq.gob.Gob;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Resolver implements Expr.Visitor<Void>, Stmt.Visitor<Void>{
    private final Interpreter interpreter;
    private final Stack<Map<String, Boolean>> scopes = new Stack<>();
    private enum ClassType {
        NONE,
        CLASS,
        SUBCLASS

    }
    private ClassType currentClass = ClassType.NONE;

    private enum FunctionType {
        NONE,
        FUNCTION,
        METHOD,
        INITIALIZER,

    }
    private FunctionType currentFunction = FunctionType.NONE;


    public Resolver(Interpreter interpreter) {
        this.interpreter = interpreter;
    }
    public void resolve(List<Stmt> statements) {
        for (Stmt statement : statements) {
            resolve(statement);
        }
    }
    private void resolve(Stmt stmt) {
        stmt.accept(this);
    }
    private void resolve(Expr expr) {
        expr.accept(this);
    }
    private void resolveLocal(Expr expr, Token name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name.lexeme)) {
                interpreter.resolve(expr, scopes.size() - 1 - i);
                return;
            }
        }
    }
    private void resolveFunction(Stmt.Function function, FunctionType type) {
        FunctionType enclosingFunction = currentFunction;
        currentFunction = type;
        beginScope();
        for (Token param : function.params) {
            declare(param);
            define(param);
        }
        resolve(function.body);
        endScope();
        currentFunction = enclosingFunction;
    }
    private void beginScope() {
        scopes.push(new HashMap<String, Boolean>());
    }
    private void endScope() {
        scopes.pop();
    }
    private void declare(Token name) {
        if (scopes.isEmpty()) return;
        Map<String, Boolean> scope = scopes.peek();
        if (scope.containsKey(name.lexeme)) {
            Gob.error(name,
                    "Doorsamahan Horaa Loo Magacaabay");
        }
        scope.put(name.lexeme, false);
    }
    private void define(Token name) {
        if (scopes.isEmpty()) return;
        scopes.peek().put(name.lexeme, true);
    }
    @Override
    public Void visit(Expr.Binary expr) {
        resolve(expr.left);
        resolve(expr.right);
        return null;
    }

    @Override
    public Void visit(Expr.Grouping expr) {
        resolve(expr.expression);
        return null;    }

    @Override
    public Void visit(Expr.Literal expr) {
        return null;
    }

    @Override
    public Void visit(Expr.ListLiteral expr) {
        for (Expr element : expr.elements) {
            resolve(element);
        }
        return null;
    }

    @Override
    public Void visit(Expr.Unary expr) {
        resolve(expr.right);
        return null;
    }

    @Override
    public Void visit(Expr.Variable expr) {
        if (!scopes.isEmpty() &&
                scopes.peek().get(expr.name.lexeme) == Boolean.FALSE) {
            Gob.error(expr.name,
                    "Lama Akhrin Karo Doorsome Maagacaabistiisa Dhexdiisa.");
        }
        resolveLocal(expr, expr.name);
        return null;
    }


    @Override
    public Void visit(Expr.ListUpdate expr) {
        resolve(expr.callee);
        resolve(expr.index);
        resolve(expr.value);
        return null;
    }
    @Override
    public Void visit(Expr.ListCall expr) {
        resolve(expr.callee);
        resolve(expr.index);
        return null;
    }
    @Override
    public Void visit(Expr.Assign expr) {
        resolve(expr.value);
        resolveLocal(expr, expr.name);
        return null;
    }

    @Override
    public Void visit(Expr.CompAssign expr) {
        resolve(expr.value);
        resolveLocal(expr, expr.name);
        return null;
    }

    @Override
    public Void visit(Expr.Logical expr) {
        resolve(expr.left);
        resolve(expr.right);
        return null;
    }

    @Override
    public Void visit(Expr.Set expr) {
        resolve(expr.value);
        resolve(expr.object);
        return null;
    }

    @Override
    public Void visit(Expr.Super expr) {
        if (currentClass == ClassType.NONE) {
            Gob.error(expr.keyword,
                    "Lama Istimaali karo 'ab' Dibada cayn");
        } else if (currentClass != ClassType.SUBCLASS) {
            Gob.error(expr.keyword,
                    "Lama Istimaali karo 'ab' cayn Aan Waalid Lahayn. ");
        }
        resolveLocal(expr, expr.keyword);
        return null;
    }

    @Override
    public Void visit(Expr.This expr) {
        if (currentClass == ClassType.NONE) {
            Gob.error(expr.keyword,
                    "Lama Istimaali karo 'kan' Dibada cayn");
            return null;
        }
        resolveLocal(expr, expr.keyword);
        return null;
    }

    @Override
    public Void visit(Expr.Call expr) {
        resolve(expr.callee);
        for (Expr argument : expr.arguments) {
            resolve(argument);
        }
        return null;
    }

    @Override
    public Void visit(Stmt.Block stmt) {
        beginScope();
        resolve(stmt.statements);
        endScope();
        return null;
    }

    @Override
    public Void visit(Stmt.Class stmt) {
        ClassType enclosingClass = currentClass;
        currentClass = ClassType.CLASS;
        declare(stmt.name);
        define(stmt.name);
        if (stmt.superclass != null &&
                stmt.name.lexeme.equals(stmt.superclass.name.lexeme)) {
            Gob.error(stmt.superclass.name,
                    "cayn Isagu Isma Dhaxlo. ");
        }
        if (stmt.superclass != null) {
            currentClass = ClassType.SUBCLASS;
            resolve(stmt.superclass);
        }
        if (stmt.superclass != null) {
            beginScope();
            scopes.peek().put("ab", true);
        }
        beginScope();
        scopes.peek().put("kan", true);
        for (Object method : stmt.methods) {
            if(method instanceof Stmt.Function) {
                FunctionType declaration = FunctionType.METHOD;

                if (((Stmt.Function) method).name.lexeme.equals(stmt.name.lexeme)) {
                    declaration = FunctionType.INITIALIZER;
                }
                resolveFunction(((Stmt.Function) method), declaration);
            } else if (method instanceof Stmt.Var) {
                declare(((Stmt.Var) method).name);
                if (((Stmt.Var) method).initializer != null) {
                    resolve(((Stmt.Var) method).initializer);
                }
                define(((Stmt.Var) method).name);
            }
        }
        endScope();
        if (stmt.superclass != null) endScope();
        currentClass = enclosingClass;
        return null;
    }

    @Override
    public Void visit(Expr.Get expr) {
        resolve(expr.object);
        return null;
    }

    @Override
    public Void visit(Stmt.Expression stmt) {
        resolve(stmt.expression);
        return null;
    }

    @Override
    public Void visit(Stmt.PrintLN stmt) {
        resolve(stmt.expression);
        return null;
    }

    @Override
    public Void visit(Stmt.Print stmt) {
        resolve(stmt.expression);
        return null;    }



    @Override
    public Void visit(Expr.Length expr) {
        resolve(expr.expression);
        return null;
    }

    @Override
    public Void visit(Stmt.Var stmt) {
        declare(stmt.name);
        if (stmt.initializer != null) {
            resolve(stmt.initializer);
        }
        define(stmt.name);
        return null;
    }

    @Override
    public Void visit(Stmt.Listing stmt) {
        declare(stmt.name);
        if (stmt.initializer != null) {
            for (Expr element : stmt.initializer) {
                resolve(element);
            }
        }
        define(stmt.name);
        return null;
    }

    @Override
    public Void visit(Stmt.Function stmt) {
        declare(stmt.name);
        define(stmt.name);
        resolveFunction(stmt, FunctionType.FUNCTION);
        return null;
    }

    @Override
    public Void visit(Stmt.Return stmt) {
        if (currentFunction == FunctionType.NONE) {
            Gob.error(stmt.keyword, "Waxba Lamasoo Celiyo Halkan. ");
        }
        if (currentFunction == FunctionType.INITIALIZER) {
            Gob.error(stmt.keyword, "Waxba Lamasoo Celiyo Halkan. ");
        }
        if (stmt.value != null) {
            resolve(stmt.value);
        }
        return null;
    }

    @Override
    public Void visit(Stmt.If stmt) {
        resolve(stmt.condition);
        resolve(stmt.thenBranch);
        if (stmt.elseIF != null) resolve(stmt.elseIF);
        if (stmt.elseBranch != null) resolve(stmt.elseBranch);
        return null;
    }

    @Override
    public Void visit(Stmt.Else stmt) {
        return null;
    }

    @Override
    public Void visit(Stmt.While stmt) {
        resolve(stmt.condition);
        resolve(stmt.body);
        return null;
    }
}
