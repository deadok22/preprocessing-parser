package ru.spbau.preprocessing.typechef.erlang

import de.fosd.typechef.parser.c.{AST, PrettyPrinter, CParser}

object Launcher extends App {
  parseAndPrintC()

  def parseAndPrintC() = {
    val parser = new CParser()

    parser.parse("int foo() { return 10; }", parser.translationUnit) match {
      case parser.Success(ast : AST, remaining) => {
        println(PrettyPrinter.print(ast))
      }
      case parser.NoSuccess(msg, remaining, somethingElse) => {
        println(msg)
      }
    }
  }
}