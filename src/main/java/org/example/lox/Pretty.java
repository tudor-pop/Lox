package org.example.lox;

import org.example.lox.AstPrinter;
import org.example.lox.Expr;
import org.example.lox.Token;
import org.example.lox.TokenType;

public class Pretty {
    public static void main(String[] args) {
        Expr expression = new Expr.Binary(
                new Expr.Unary(
                        new Token(TokenType.MINUS, "-", null, 1),
                        new Expr.Literal(123)),
                new Token(TokenType.STAR, "*", null, 1),
                new Expr.Grouping(
                        new Expr.Literal(45.67)));
        System.out.println(new AstPrinter().print(expression));
    }
}
