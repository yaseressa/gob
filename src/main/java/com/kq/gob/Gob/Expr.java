package com.kq.gob.Gob;


import java.util.List;
import java.util.ArrayList;

public abstract class Expr {
 interface Visitor<R> {
 R visit(Assign expr);
 R visit(CompAssign expr);
 R visit(Binary expr);
 R visit(Call expr);
 R visit(Get expr);
 R visit(Grouping expr);
 R visit(Literal expr);
 R visit(Logical expr);
 R visit(Set expr);
 R visit(Super expr);
 R visit(This expr);
 R visit(Unary expr);
 R visit(Variable expr);
 R visit(ListCall expr);
 R visit(ListUpdate expr);
 R visit(ListLiteral expr);
 R visit(Length expr);
 }
public static class Assign extends Expr {
 public Assign(Token name, Expr value) {
 this.name = name;
 this.value = value;
 }

 @Override
 <R> R accept(Visitor<R> visitor) {
 return visitor.visit(this);
 }

 final public Token name;
 final public Expr value;
 }
public static class CompAssign extends Expr {
 public CompAssign(Token name, Token operator, Expr value) {
 this.name = name;
 this.operator = operator;
 this.value = value;
 }

 @Override
 <R> R accept(Visitor<R> visitor) {
 return visitor.visit(this);
 }

 final public Token name;
 final public Token operator;
 final public Expr value;
 }
public static class Binary extends Expr {
 public Binary(Expr left, Token operator, Expr right) {
 this.left = left;
 this.operator = operator;
 this.right = right;
 }

 @Override
 <R> R accept(Visitor<R> visitor) {
 return visitor.visit(this);
 }

 final public Expr left;
 final public Token operator;
 final public Expr right;
 }
public static class Call extends Expr {
 public Call(Expr callee, Token paren, List<Expr> arguments) {
 this.callee = callee;
 this.paren = paren;
 this.arguments = arguments;
 }

 @Override
 <R> R accept(Visitor<R> visitor) {
 return visitor.visit(this);
 }

 final public Expr callee;
 final public Token paren;
 final public List<Expr> arguments;
 }
public static class Get extends Expr {
 public Get(Expr object, Token name) {
 this.object = object;
 this.name = name;
 }

 @Override
 <R> R accept(Visitor<R> visitor) {
 return visitor.visit(this);
 }

 final public Expr object;
 final public Token name;
 }
public static class Grouping extends Expr {
 public Grouping(Expr expression) {
 this.expression = expression;
 }

 @Override
 <R> R accept(Visitor<R> visitor) {
 return visitor.visit(this);
 }

 final public Expr expression;
 }
public static class Literal extends Expr {
 public Literal(Object value) {
 this.value = value;
 }

 @Override
 <R> R accept(Visitor<R> visitor) {
 return visitor.visit(this);
 }

 final public Object value;
 }
public static class Logical extends Expr {
 public Logical(Expr left, Token operator, Expr right) {
 this.left = left;
 this.operator = operator;
 this.right = right;
 }

 @Override
 <R> R accept(Visitor<R> visitor) {
 return visitor.visit(this);
 }

 final public Expr left;
 final public Token operator;
 final public Expr right;
 }
public static class Set extends Expr {
 public Set(Expr object, Token name, Expr value) {
 this.object = object;
 this.name = name;
 this.value = value;
 }

 @Override
 <R> R accept(Visitor<R> visitor) {
 return visitor.visit(this);
 }

 final public Expr object;
 final public Token name;
 final public Expr value;
 }
public static class Super extends Expr {
 public Super(Token keyword, Token method) {
 this.keyword = keyword;
 this.method = method;
 }

 @Override
 <R> R accept(Visitor<R> visitor) {
 return visitor.visit(this);
 }

 final public Token keyword;
 final public Token method;
 }
public static class This extends Expr {
 public This(Token keyword) {
 this.keyword = keyword;
 }

 @Override
 <R> R accept(Visitor<R> visitor) {
 return visitor.visit(this);
 }

 final public Token keyword;
 }
public static class Unary extends Expr {
 public Unary(Token operator, Expr right) {
 this.operator = operator;
 this.right = right;
 }

 @Override
 <R> R accept(Visitor<R> visitor) {
 return visitor.visit(this);
 }

 final public Token operator;
 final public Expr right;
 }
public static class Variable extends Expr {
 public Variable(Token name) {
 this.name = name;
 }

 @Override
 <R> R accept(Visitor<R> visitor) {
 return visitor.visit(this);
 }

 final public Token name;
 }
public static class ListCall extends Expr {
 public ListCall(Expr callee, Expr index, Token bracket) {
 this.callee = callee;
 this.index = index;
 this.bracket = bracket;
 }

 @Override
 <R> R accept(Visitor<R> visitor) {
 return visitor.visit(this);
 }

 final public Expr callee;
 final public Expr index;
 final public Token bracket;
 }
public static class ListUpdate extends Expr {
 public ListUpdate(Expr callee, Expr index, Expr value, Token bracket) {
 this.callee = callee;
 this.index = index;
 this.value = value;
 this.bracket = bracket;
 }

 @Override
 <R> R accept(Visitor<R> visitor) {
 return visitor.visit(this);
 }

 final public Expr callee;
 final public Expr index;
 final public Expr value;
 final public Token bracket;
 }
public static class ListLiteral extends Expr {
 public ListLiteral(List<Expr> elements) {
 this.elements = elements;
 }

 @Override
 <R> R accept(Visitor<R> visitor) {
 return visitor.visit(this);
 }

 final public List<Expr> elements;
 }

public static class Length extends Expr {
 public Length(Expr expression) {
 this.expression = expression;
 }

 @Override
 <R> R accept(Visitor<R> visitor) {
 return visitor.visit(this);
 }

 final public Expr expression;
 }

 abstract <R> R accept(Visitor<R> visitor);
}
