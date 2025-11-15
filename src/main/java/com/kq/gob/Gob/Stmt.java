package com.kq.gob.Gob;


import java.util.List;
import java.util.ArrayList;

public abstract class Stmt {
 interface Visitor<R> {
 R visit(Block stmt);
 R visit(Class stmt);
 R visit(Expression stmt);
 R visit(PrintLN stmt);
 R visit(Print stmt);
 R visit(Var stmt);
 R visit(Listing stmt);
 R visit(Function stmt);
 R visit(Return stmt);
 R visit(If stmt);
 R visit(Else stmt);
 R visit(While stmt);
 }
public static class Block extends Stmt {
 public Block(List<Stmt> statements) {
 this.statements = statements;
 }

 @Override
 <R> R accept(Visitor<R> visitor) {
 return visitor.visit(this);
 }

 final public List<Stmt> statements;
 }
public static class Class extends Stmt {
 public Class(Token name, Expr.Variable superclass, List<Object> methods) {
 this.name = name;
 this.superclass = superclass;
 this.methods = methods;
 }

 @Override
 <R> R accept(Visitor<R> visitor) {
 return visitor.visit(this);
 }

 final public Token name;
 final public Expr.Variable superclass;
 final public List<Object> methods;
 }
public static class Expression extends Stmt {
 public Expression(Expr expression) {
 this.expression = expression;
 }

 @Override
 <R> R accept(Visitor<R> visitor) {
 return visitor.visit(this);
 }

 final public Expr expression;
 }
public static class PrintLN extends Stmt {
 public PrintLN(Expr expression) {
 this.expression = expression;
 }

 @Override
 <R> R accept(Visitor<R> visitor) {
 return visitor.visit(this);
 }

 final public Expr expression;
 }
public static class Print extends Stmt {
 public Print(Expr expression) {
 this.expression = expression;
 }

 @Override
 <R> R accept(Visitor<R> visitor) {
 return visitor.visit(this);
 }

 final public Expr expression;
 }
public static class Var extends Stmt {
 public Var(Token name, Expr initializer) {
 this.name = name;
 this.initializer = initializer;
 }

 @Override
 <R> R accept(Visitor<R> visitor) {
 return visitor.visit(this);
 }

 final public Token name;
 final public Expr initializer;
 }
public static class Listing extends Stmt {
 public Listing(Token name, ArrayList<Expr> initializer) {
 this.name = name;
 this.initializer = initializer;
 }

 @Override
 <R> R accept(Visitor<R> visitor) {
 return visitor.visit(this);
 }

 final public Token name;
 final public ArrayList<Expr> initializer;
 }
public static class Function extends Stmt {
 public Function(Token name, List<Token> params, List<Stmt> body) {
 this.name = name;
 this.params = params;
 this.body = body;
 }

 @Override
 <R> R accept(Visitor<R> visitor) {
 return visitor.visit(this);
 }

 final public Token name;
 final public List<Token> params;
 final public List<Stmt> body;
 }
public static class Return extends Stmt {
 public Return(Token keyword, Expr value) {
 this.keyword = keyword;
 this.value = value;
 }

 @Override
 <R> R accept(Visitor<R> visitor) {
 return visitor.visit(this);
 }

 final public Token keyword;
 final public Expr value;
 }
public static class If extends Stmt {
 public If(Expr condition, Stmt thenBranch, Stmt elseBranch, Stmt elseIF) {
 this.condition = condition;
 this.thenBranch = thenBranch;
 this.elseBranch = elseBranch;
 this.elseIF = elseIF;
 }

 @Override
 <R> R accept(Visitor<R> visitor) {
 return visitor.visit(this);
 }

 final public Expr condition;
 final public Stmt thenBranch;
 final public Stmt elseBranch;
 final public Stmt elseIF;
 }
public static class Else extends Stmt {
 public Else(Stmt statement) {
 this.statement = statement;
 }

 @Override
 <R> R accept(Visitor<R> visitor) {
 return visitor.visit(this);
 }

 final public Stmt statement;
 }
public static class While extends Stmt {
 public While(Expr condition, Stmt body) {
 this.condition = condition;
 this.body = body;
 }

 @Override
 <R> R accept(Visitor<R> visitor) {
 return visitor.visit(this);
 }

 final public Expr condition;
 final public Stmt body;
 }

 abstract <R> R accept(Visitor<R> visitor);
}
