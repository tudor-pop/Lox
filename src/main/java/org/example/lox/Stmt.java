package org.example.lox;

import java.util.List;
/*
***************************************************************************************************************
* This file was generated. Any modifications will be overriden next time the GenerateAst tool is being run. ***
***************************************************************************************************************
*/

public abstract class Stmt {

  abstract <R> R accept(Visitor<R> visitor);
  interface Visitor<R> {
    R visit(Expression stmt);
    R visit(Print stmt);
  }
  static class Expression extends Stmt {
    Expression(Expr expression) {
      this.expression = expression;
    }

    final Expr expression;

	@Override
	<R> R accept(Visitor<R> visitor) {
	     return visitor.visit(this);
	}

  }
  static class Print extends Stmt {
    Print(Expr expression) {
      this.expression = expression;
    }

    final Expr expression;

	@Override
	<R> R accept(Visitor<R> visitor) {
	     return visitor.visit(this);
	}

  }
}
