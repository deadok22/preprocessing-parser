package ru.spbau.preprocessing.benchmark

import ru.spbau.preprocessing.lexer.lexemegraph.LexemeGraphNode

trait ParserRunner {
  type Input
  def prepareLexemes(node: LexemeGraphNode): Input
  def parse(input: Input): Boolean
}
