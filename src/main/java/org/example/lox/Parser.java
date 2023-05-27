package org.example.lox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.example.lox.TokenType.*;


/**
 * Name         Operators       Associates
 * Equality     == !=           Left
 * Comparison   < >= < <=       Left
 * Term         - +             Left
 * Factor       * /             Left
 * Unary        ! -             Right
 * Each rule here only matches expressions at its precedence level or higher.
 * For example, unary matches a unary expression like !negated or a primary expression like 1234
 * And term can match 1 + 2 but also 3 * 4 / 5. The final primary rule covers the highest-precedence
 * forms—literals and parenthesized expressions.
 * <p>
 * Expression -> Equality
 * Equality -> Comparison ( ("==" | "!=") Comparison)* ;
 * Comparison -> Term ( ( "!=" | "==" ) Term )* ;
 * Term -> Factor ( ( "-" | "+" ) Factor )* ;
 * Factor -> Unary ( ( "/" | "*" ) Unary )* ;
 * Unary -> ( "!" | "-" ) Unary
 * | Primary ;
 * Primary -> NUMBER
 * | STRING
 * | "true"
 * | "false"
 * | "null"
 * | "(" Expression ")"
 * <p>
 * ----------------------------------------------------------
 * Grammar notation         Code representation
 * -----------------------------------------------------------
 * Terminal                 Code to match and consume a token
 * Nonterminal              Call to that rule’s function
 * |                        if or switch statement
 * + or *                   while or for loop
 * ?                        if statement
 * ------------------------------------------------------------
 */
class Parser {
    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();
        try {
            while (!isAtEnd()) {
                statements.add(Statement());
            }
        } catch (ParseError error) {
            return Collections.emptyList();
        }
        return statements;
    }

    private Stmt Statement() {
        if (match(PRINT)) return PrintStatement();

        return ExpressionStatement();
    }

    private Stmt ExpressionStatement() {
        var value = Expression();
        consume(SEMICOLON, "Expect ';' after value.");
        return new Stmt.Expression(value);
    }

    private Stmt PrintStatement() {
        Expr value = Expression();
        consume(SEMICOLON, "Expect ';' after value.");
        return new Stmt.Print(value);
    }

    /*
     * Each method for parsing a grammar rule produces a syntax tree for that rule and returns it to the caller.
     * When the body of the rule contains a nonterminal — a reference to another rule — we call that other rule’s method.
     * */
    private Expr Expression() {
        return Equality();
    }

    /**
     * equality → comparison ( ( "!=" | "==" ) comparison )* ;
     * The first comparison nonterminal in the body translates to the first call to comparison() in the method. We take that result and store it in a local variable.
     * Then, the ( ... )* loop in the rule maps to a while loop. We need to know when to exit that loop.
     * We can see that inside the rule, we must first find either a != or == token.
     * So, if we don’t see one of those, we must be done with the sequence of equality operators.
     * We express that check using a handy match() method
     * The parser falls out of the loop once it hits a token that’s not an equality operator.
     * Finally, it returns the expression. Note that if the parser never encounters an equality operator, then it never enters the loop.
     * In that case, the equality() method effectively calls and returns comparison().
     * In that way, this method matches an equality operator or anything of higher precedence.
     */
    private Expr Equality() {
        Expr expr = Comparison();
        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            Token operator = previous();
            Expr right = Comparison();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr Comparison() {
        Expr expr = Term();
        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = previous();
            Expr right = Term();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr Term() {
        Expr expr = Factor();
        while (match(MINUS, PLUS)) {
            Token operator = previous();
            Expr right = Factor();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr Factor() {
        Expr expr = Unary();
        while (match(SLASH, STAR)) {
            Token operator = previous();
            Expr right = Unary();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr Unary() {
        if (match(BANG, MINUS)) {
            Token operator = previous();
            Expr right = Unary();
            return new Expr.Unary(operator, right);
        }
        return Primary();
    }

    private Expr Primary() {
        if (match(FALSE)) return new Expr.Literal(false);
        if (match(TRUE)) return new Expr.Literal(true);
        if (match(NIL)) return new Expr.Literal(null);
        if (match(NUMBER, STRING)) {
            return new Expr.Literal(previous().literal);
        }
        if (match(LEFT_PAREN)) {
            Expr expr = Expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }
        throw error(peek(), "Expect expression.");
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();
        throw error(peek(), message);
    }

    private ParseError error(Token token, String message) {
        Lox.error(token, message);
        return new ParseError();
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

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private static class ParseError extends RuntimeException {
    }

    private void synchronize() {
        advance();
        while (!isAtEnd()) {
            if (previous().type == SEMICOLON) return;
            switch (peek().type) {
                case CLASS, FUN, VAR, FOR, IF, WHILE, PRINT, RETURN -> {
                    return;
                }
            }
            advance();
        }
    }


}
