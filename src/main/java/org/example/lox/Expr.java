package org.example.lox;

import java.util.List;
/*
***************************************************************************************************************
* This file was generated. Any modifications will be overriden next time the GenerateAst tool is being run. ***
***************************************************************************************************************
*/

public abstract class Expr {

  abstract <R> R accept(Visitor<R> visitor);
  interface Visitor<R> {
    R visit(Binary expr);
    R visit(Grouping expr);
    R visit(Literal expr);
    R visit(Unary expr);
  }
  static class Binary extends Expr {
    Binary(Expr left, Token operator, Expr right) {
      this.left = left;
      this.operator = operator;
      this.right = right;
    }

    final Expr left;
    final Token operator;
    final Expr right;

	@Override
	<R> R accept(Visitor<R> visitor) {
	     return visitor.visit(this);
	}

  }
  static class Grouping extends Expr {
    Grouping(Expr expression) {
      this.expression = expression;
    }

    final Expr expression;

	@Override
	<R> R accept(Visitor<R> visitor) {
	     return visitor.visit(this);
	}

  }
  static class Literal extends Expr {
    Literal(Object value) {
      this.value = value;
    }

    final Object value;

	@Override
	<R> R accept(Visitor<R> visitor) {
	     return visitor.visit(this);
	}

  }
  static class Unary extends Expr {
    Unary(Token operator, Expr right) {
      this.operator = operator;
      this.right = right;
    }

    final Token operator;
    final Expr right;

	@Override
	<R> R accept(Visitor<R> visitor) {
	     return visitor.visit(this);
	}

  }
}
