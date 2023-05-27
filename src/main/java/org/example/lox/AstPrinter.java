package org.example.lox;

import java.util.List;

class AstPrinter implements Expr.Visitor<String>, Stmt.Visitor<String> {
    String print(Expr expr) {
        return expr.accept(this);
    }

    String print(List<Stmt> stmts) {
        var res = "";
        for (Stmt stmt : stmts) {
            res += stmt.accept(this);
        }
        return res;
    }

    @Override
    public String visit(Expr.Binary expr) {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String visit(Expr.Grouping expr) {
        return parenthesize("group", expr.expression);
    }

    @Override
    public String visit(Expr.Literal expr) {
        if (expr.value == null) return "nil";
        return expr.value.toString();
    }

    @Override
    public String visit(Expr.Unary expr) {
        return parenthesize(expr.operator.lexeme, expr.right);
    }

    private String parenthesize(String name, Expr... exprs) {
        var builder = new StringBuilder();
        builder.append("(")
                .append(name);
        for (Expr expr : exprs) {
            builder.append(" ")
                    .append(expr.accept(this));
        }
        builder.append(")");
        return builder.toString();
    }

    @Override
    public String visit(Stmt.Expression stmt) {
        return "list " + stmt.expression.accept(this);
    }

    @Override
    public String visit(Stmt.Print stmt) {
        return "print "+stmt.expression.accept(this);
    }
}