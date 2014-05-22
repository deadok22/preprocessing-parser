package ru.spbau.preprocessing.benchmark

import ru.spbau.preprocessing.parser.earley.parser.EarleyParser
import ru.spbau.preprocessing.erlang.ErlangEarleyGrammar
import ru.spbau.preprocessing.parser.earley.grammar.EarleyGrammar
import ru.spbau.preprocessing.erlang.conditions.ErlangPresenceConditionFactory
import ru.spbau.preprocessing.lexer.lexemegraph.LexemeGraphNode

object EarleyParserRunner extends ParserRunner {
  type Input = LexemeGraphNode

  override def parse(lexemeGraph: Input): Boolean = {
    val grammar: EarleyGrammar = ErlangEarleyGrammar.createGrammar()
    val parser = new EarleyParser(grammar, new ErlangPresenceConditionFactory)
    parser.parse(lexemeGraph) != null
  }

  override def prepareLexemes(node: LexemeGraphNode): Input = node
}
