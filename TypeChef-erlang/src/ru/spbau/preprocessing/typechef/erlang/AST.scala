package ru.spbau.preprocessing.typechef.erlang

import de.fosd.typechef.parser.WithPosition
import de.fosd.typechef.conditional.Opt

trait AST extends Product with Serializable with Cloneable with WithPosition {
  override def clone(): AST.this.type = super.clone().asInstanceOf[AST.this.type]
}


trait Form extends AST

trait Expr extends AST


case class Atomic(atom: String) extends Expr

case class Exprs(exprs: List[Opt[Expr]]) extends Expr


case class FunctionClause(atom: String, body: Exprs) extends AST

case class Function(clauses: List[Opt[FunctionClause]]) extends Form


case class File(forms: List[Opt[Form]]) extends AST