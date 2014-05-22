package ru.spbau.preprocessing.benchmark

import java.io.File
import ru.spbau.preprocessing.parser.earley.parser.EarleyParser
import ru.spbau.preprocessing.erlang.{ErlangLanguageProvider, ErlangToken, ErlangEarleyGrammar}
import ru.spbau.preprocessing.parser.earley.grammar.EarleyGrammar
import ru.spbau.preprocessing.erlang.conditions.ErlangPresenceConditionFactory
import ru.spbau.preprocessing.erlang.files.ErlangFileSystemSourceFile
import ru.spbau.preprocessing.lexer.PreprocessingLexer
import ru.spbau.preprocessing.lexer.lexemegraph.LexemeGraphNode

object EarleyParserRunner extends ParserRunner {
  override def parse(file: File): Boolean = {
    val grammar: EarleyGrammar = ErlangEarleyGrammar.createGrammar()
    val parser = new EarleyParser(grammar, new ErlangPresenceConditionFactory)
    val sourceFile: ErlangFileSystemSourceFile = new ErlangFileSystemSourceFile(file)
    val lexer: PreprocessingLexer[ErlangToken] = new PreprocessingLexer[ErlangToken](new ErlangLanguageProvider, sourceFile)
    val lexemeGraph: LexemeGraphNode = lexer.buildLexemeGraph
    parser.parse(lexemeGraph) != null
  }
}
