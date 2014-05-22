package ru.spbau.preprocessing.benchmark

import ru.spbau.preprocessing.typechef.erlang.{LexemeSequence, ErlangLexemeWrapper, ErlangParser}
import ru.spbau.preprocessing.lexer.lexemegraph.LexemeGraphNode

object TypeChefParserRunner extends ParserRunner {
  type Input = List[ErlangLexemeWrapper]


  override def parse(input: Input): Boolean = {
    val parser = new ErlangParser
    val production = parser.file
    parser.isSuccessfulParse(parser.parse(input, production))
  }

  override def prepareLexemes(node: LexemeGraphNode): Input = LexemeSequence.from(node)
}
