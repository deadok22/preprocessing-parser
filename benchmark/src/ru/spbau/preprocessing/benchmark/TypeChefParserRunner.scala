package ru.spbau.preprocessing.benchmark

import ru.spbau.preprocessing.typechef.erlang.ErlangParser
import java.io

object TypeChefParserRunner extends ParserRunner {
  override def parse(file: io.File): Boolean = {
    val parser = new ErlangParser
    val production = parser.file
    parser.isSuccessfulParse(parser.parseFile(file, production))
  }
}
