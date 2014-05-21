package ru.spbau.preprocessing.typechef.erlang

import de.fosd.typechef.parser.{TokenReader, MultiFeatureParser}
import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureExpr}
import ru.spbau.preprocessing.erlang.files.ErlangFileSystemSourceFile
import ru.spbau.preprocessing.lexer.PreprocessingLexer
import ru.spbau.preprocessing.erlang.{ErlangToken, ErlangLanguageProvider}
import ru.spbau.preprocessing.erlang.ErlangToken._
import ru.spbau.preprocessing.lexer.lexemegraph.LexemeGraphNode

class ErlangParser extends MultiFeatureParser {
  type Elem = ErlangLexemeWrapper

  def parseFile[T](file: java.io.File, production: (TokenReader[Elem, Null], FeatureExpr) => MultiParseResult[T]): MultiParseResult[T] = {
    val sourceFile: ErlangFileSystemSourceFile = new ErlangFileSystemSourceFile(file)
    val lexer: PreprocessingLexer[ErlangToken] = new PreprocessingLexer[ErlangToken](new ErlangLanguageProvider, sourceFile)
    val graph: LexemeGraphNode = lexer.buildLexemeGraph
    val lexemes: List[Elem] = LexemeSequence.from(graph)
    val tokenReader: TokenReader[Elem, Null] = new TokenReader[Elem, Null](lexemes, 0, eofToken = null)
    production(tokenReader, FeatureExprFactory.True)
  }

  def file: MultiParser[File] = repOpt(form, "forms") ^^ {
    new File(_)
  }

  def form: MultiParser[Form] = function <~ dot

  def function: MultiParser[Function] = rep1SepOpt(functionClause, semicolon) ^^ {
    new Function(_)
  }

  def functionClause: MultiParser[FunctionClause] = atom ~ clauseArgs ~ clauseBody ^^ {
    case (clauseName, _, body) => new FunctionClause(clauseName, body)
  }

  def clauseArgs = lpar ~ rpar

  def clauseBody = arrow ~> exprs ^^ {
    new Exprs(_)
  }

  def exprs = rep1SepOpt(expr, comma)

  def expr = atomic

  def atomic = atom ^^ {
    new Atomic(_)
  }

  // tokens

  def erlangToken(kind: String, t: ErlangToken) = token(kind, {
    _.getType == t
  })

  def erlangTextToken(kind: String, t: ErlangToken) = erlangToken(kind, t) ^^ {
    _.getText
  }

  def dot = erlangToken("dot", DOT)

  def lpar = erlangToken("lpar", PAR_LEFT)

  def rpar = erlangToken("rpar", PAR_RIGHT)

  def atom = erlangTextToken("atom", ATOM)

  def arrow = erlangToken("arrow", ARROW)

  def comma = erlangToken("comma", COMMA)

  def semicolon = erlangToken("semicolon", SEMI)
}
