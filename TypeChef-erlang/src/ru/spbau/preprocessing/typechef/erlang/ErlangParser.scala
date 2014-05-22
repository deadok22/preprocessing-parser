package ru.spbau.preprocessing.typechef.erlang

import de.fosd.typechef.parser.{~, TokenReader, MultiFeatureParser}
import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureExpr}
import ru.spbau.preprocessing.erlang.files.ErlangFileSystemSourceFile
import ru.spbau.preprocessing.lexer.PreprocessingLexer
import ru.spbau.preprocessing.erlang.{ErlangToken, ErlangLanguageProvider}
import ru.spbau.preprocessing.erlang.ErlangToken._
import ru.spbau.preprocessing.lexer.lexemegraph.LexemeGraphNode

class ErlangParser extends MultiFeatureParser {
  type Elem = ErlangLexemeWrapper
  type TypeContext = Null

  def parseFile[T](file: java.io.File, production: (TokenReader[Elem, Null], FeatureExpr) => MultiParseResult[T]): MultiParseResult[T] = {
    val sourceFile: ErlangFileSystemSourceFile = new ErlangFileSystemSourceFile(file)
    val lexer: PreprocessingLexer[ErlangToken] = new PreprocessingLexer[ErlangToken](new ErlangLanguageProvider, sourceFile)
    val graph: LexemeGraphNode = lexer.buildLexemeGraph
    val lexemes: List[Elem] = LexemeSequence.from(graph)
    val tokenReader: TokenReader[Elem, Null] = new TokenReader[Elem, Null](lexemes, 0, eofToken = ErlangLexemeWrapper.EOF)
    production(tokenReader, FeatureExprFactory.True)
  }

  def file: MultiParser[File] = repOpt(form, "forms") ^^ {
    new File(_)
  }

  def form: MultiParser[Form] = function <~ dot

  def function: MultiParser[Function] = rep1SepOpt(functionClause, semicolon) ^^ {
    new Function(_)
  }

  def functionClause: MultiParser[FunctionClause] = atomToken ~ argumentsList ~ clauseBody ^^ {
    case clauseName ~ params ~ body => new FunctionClause(clauseName, params, body)
  }

  def argumentsList = (lpar ~> exprs ?) <~ rpar ^^ {
    new ArgumentsList(_)
  }

  def clauseBody = arrow ~> exprs

  def exprs = rep1SepOpt(expr, comma) ^^ {
    new Exprs(_)
  }

  def expr: MultiParser[Expr] = //TODO uop(CATCH, expr) |
    expr_100

  def expr_100: MultiParser[Expr] = assignment | send | expr_150

  def assignment = bop(expr_150, OP_EQ, expr_100)

  def send = bop(expr_150, OP_EXL, expr_100)

  def expr_150: MultiParser[Expr] = //TODO orelse |
    expr_160

  def orelse = bop(expr_160, ORELSE, expr_150)

  def expr_160: MultiParser[Expr] = //TODO andalso |
    expr_200

  def andalso = bop(expr_200, ANDALSO, expr_160)

  def expr_200: MultiParser[Expr] = //TODO bop(expr_300, comp_op, expr_300) |
    expr_300

  def comp_op = et(OP_EQ_EQ) | et(OP_DIV_EQ) | et(OP_EQ_LT) | et(OP_LT) | et(OP_GT_EQ) | et(OP_GT) | et(OP_EQ_COL_EQ) | et(OP_EQ_DIV_EQ)

  def expr_300: MultiParser[Expr] = //TODO bop(expr_400, list_op, expr_300) |
    expr_400

  def list_op = et(OP_PLUS_PLUS) | et(OP_MINUS_MINUS)

  def expr_400: MultiParser[Expr] = //TODO bop(expr_400, add_op, expr_500) |
    expr_500

  def add_op = et(OP_PLUS) | et(OP_MINUS) | et(BOR) | et(BXOR) | et(BSL) | et(BSR) | et(OR) | et(XOR)

  def expr_500: MultiParser[Expr] = //TODO bop(expr_500, mult_op, expr_600) |
    expr_600

  def mult_op = et(OP_AR_DIV) | et(OP_AR_MUL) | et(DIV) | et(REM) | et(BAND) | et(AND)

  def expr_600: MultiParser[Expr] = //TODO uop(prefix_op, expr_700) |
    expr_700

  def prefix_op = et(OP_PLUS) | et(OP_MINUS) | et(BNOT) | et(NOT)

  def expr_700: MultiParser[Expr] = //TODO uncomment and add parsers: function_call | record_expr | 
    expr_800

  def expr_800 = //TODO bop(expr_max, COLON, expr_max) |
    expr_max

  def expr_max = variable | atomic | parenthesized | begin_end

  def parenthesized = ((lpar ~> expr) <~ rpar) ^^ {
    new ParenthesizedExpr(_)
  }

  def begin_end = (et(BEGIN) ~> exprs) <~ et(END) ^^ {
    new BeginEndExpr(_)
  }

  def variable = variableToken ^^ {
    new Variable(_)
  }

  def atomic = char | integer | float | atom | strings

  def atom = atomToken ^^ {
    new Atom(_)
  }

  def char = ett("char", CHAR) ^^ {
    new CharExpr(_)
  }

  def integer = ett("int", INTEGER) ^^ {
    new IntegerExpr(_)
  }

  def float = ett("float", FLOAT) ^^ {
    new FloatExpr(_)
  }

  def strings = repOpt(ett("string", STRING)) ^^ {
    new StringsExpr(_)
  }

  def bop[Left <: Expr, Right <: Expr](l: => MultiParser[Left], op: ErlangToken, r: => MultiParser[Right]): MultiParser[BinaryExpr] =
    bop(l, et(op.toString.toLowerCase, op), r)

  def bop[Left <: Expr, Right <: Expr](l: => MultiParser[Left], op: MultiParser[Elem], r: => MultiParser[Right]): MultiParser[BinaryExpr] =
    l ~ op ~ r ^^ {
      case a ~ o ~ b => new BinaryExpr(a, b, o.getType)
    }

  def uop[Arg <: Expr](op: ErlangToken, a: => MultiParser[Arg]): MultiParser[PrefixExpr] = uop(et(op.toString.toLowerCase, op), a)

  def uop[Arg <: Expr](op: MultiParser[Elem], a: => MultiParser[Arg]): MultiParser[PrefixExpr] =
    op ~ a ^^ {
      case oper ~ arg => new PrefixExpr(arg, oper.getType)
    }

  // tokens

  def et(t: ErlangToken): MultiParser[Elem] = et(t.toString.toLowerCase, t)

  def et(kind: String, t: ErlangToken): MultiParser[Elem] = token(kind, {
    _.getType == t
  })

  def ett(kind: String, t: ErlangToken) = et(kind, t) ^^ {
    _.getText
  }

  def dot = et("dot", DOT)

  def lpar = et("lpar", PAR_LEFT)

  def rpar = et("rpar", PAR_RIGHT)

  def atomToken = ett("atom", ATOM)

  def variableToken = ett("var", VAR)

  def arrow = et("arrow", ARROW)

  def comma = et("comma", COMMA)

  def semicolon = et("semicolon", SEMI)
}
