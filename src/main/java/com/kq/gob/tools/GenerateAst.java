package com.kq.gob.tools;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class GenerateAst {
    public static void main(String[] args) throws IOException {
        String outputDir = args[0];
        if (outputDir.length() == 0) {
            System.err.println("Input path must be specified.");
            System.exit(64);
        }
        defineAst(outputDir, "Expr", Arrays.asList(
                "Assign : Token name, Expr value",
                "CompAssign : Token name, Token operator, Expr value",
                "Binary : Expr left, Token operator, Expr right",
                "Call : Expr callee, Token paren, List<Expr> arguments",
                "Get : Expr object, Token name",
                "Grouping : Expr expression",
                "Literal : Object value",
                "Logical : Expr left, Token operator, Expr right",
                "Set : Expr object, Token name, Expr value",
                "Super : Token keyword, Token method",
                "This : Token keyword",
                "Unary : Token operator, Expr right",
                "Variable : Token name",
                "ListCall: Expr.Variable name, Expr index",
                "ListUpdate: Expr.Variable name, Expr index, Expr value",
                "Length : Expr expression"
        ));
        defineAst(outputDir, "Stmt", Arrays.asList(
                "Block : List<Stmt> statements",
                "Class : Token name, Expr.Variable superclass, List<Object> methods",
                "Expression : Expr expression",
                "PrintLN : Expr expression",
                "Print : Expr expression",
                "Var : Token name, Expr initializer",
                "Listing : Token name, ArrayList<Expr> initializer",
                "Function : Token name, List<Token> params, List<Stmt> body",
                "Return : Token keyword, Expr value",
                "If : Expr condition, Stmt thenBranch, Stmt elseBranch, Stmt elseIF",
                "Else: Stmt statement",
                "While : Expr condition, Stmt body"
        ));
    }

    private static void defineAst(String outputDir, String baseName, List<String> types) throws IOException {
        String path = outputDir + "/" + baseName + ".java";
        PrintWriter writer = new PrintWriter(path);

        writer.println("package com.kq.gob.Gob;\n");
        writer.println();

        writer.println("import java.util.List;");
        writer.println("import java.util.ArrayList;");
        writer.println();

        writer.println("public abstract class " + baseName + " {");
        defineVisitor(writer, baseName, types);
        for (String type : types) {
            String className = type.split(":")[0].trim();
            String fields = type.split(":")[1].trim();
            defineType(writer, baseName, className, fields);
        }
        writer.println();
      writer.println(" abstract <R> R accept(Visitor<R> visitor);");
        writer.println("}");
        writer.close();
    }

    private static void defineType(PrintWriter writer, String baseName,
                                   String className, String fieldList) {

        writer.println("public static class " + className + " extends " + baseName + " {");
        writer.println(" public " + className + "(" + fieldList + ") {");
        String[] fields = Arrays.stream(fieldList.split(", ")).map(x -> "public " + x).toArray(String[]::new);

        for (String field : fields) {
            String name = field.split(" ")[2];
            writer.println(" this." + name + " = " + name + ";");
        }

        writer.println(" }");
        writer.println();
        writer.println(" @Override");
        writer.println(" <R> R accept(Visitor<R> visitor) {");
        writer.println(" return visitor.visit(this);");
        writer.println(" }");
        writer.println();
        for (String field : fields) {
            writer.println(" final " + field + ";");
        }

        writer.println(" }");

    }

    private static void defineVisitor(
            PrintWriter writer, String baseName, List<String> types) {
        writer.println(" interface Visitor<R> {");
        for (String type : types) {
            String typeName = type.split(":")[0].trim();
            writer.println(" R visit(" +
                    typeName + " " + baseName.toLowerCase() + ");");
        }

        writer.println(" }");
    }
}

