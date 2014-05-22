package ru.spbau.preprocessing.typechef.erlang

import de.fosd.typechef.parser.WithPosition
import de.fosd.typechef.conditional.Opt
import ru.spbau.preprocessing.erlang.ErlangToken

trait AST extends Product with Serializable with Cloneable with WithPosition {
  override def clone(): AST.this.type = super.clone().asInstanceOf[AST.this.type]
}

//file
trait Form extends AST

case class File(forms: List[Opt[Form]]) extends AST


//expressions
trait Expr extends AST

case class Atom(atom: String) extends Expr

case class Variable(variable: String) extends Expr

case class PrefixExpr(expr: Expr, op: ErlangToken) extends Expr

case class BinaryExpr(left: Expr, right: Expr, op: ErlangToken) extends Expr

case class ParenthesizedExpr(expr: Expr) extends Expr

case class BeginEndExpr(exprs: Exprs) extends Expr

case class IntegerExpr(integer: String) extends Expr

case class CharExpr(char: String) extends Expr

case class FloatExpr(float: String) extends Expr

case class StringsExpr(strings: List[Opt[String]]) extends Expr

case class FunctionCallExpr(callee: Expr, args: ArgumentsList) extends Expr

case class Exprs(exprs: List[Opt[Expr]]) extends Expr

//function
case class FunctionClause(atom: String, params: ArgumentsList, body: Exprs) extends AST

case class ArgumentsList(arguments: Exprs) extends AST

case class Function(clauses: List[Opt[FunctionClause]]) extends Form