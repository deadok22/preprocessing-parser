package ru.spbau.preprocessing.typechef.erlang

object Launcher extends App {
  parseAndPrintC()
  parseAndPrintJava()

  def parseAndPrintC() = {
    import de.fosd.typechef.parser.c.{PrettyPrinter, CParser}

    val parser = new CParser()

    parser.parse(
      "#ifdef Y\n" +
      "#define X 10\n" +
      "#else\n" +
      "#define X 9\n" +
      "#endif\n" +
      "int foo() { return X; }"
      , parser.translationUnit) match {
      case parser.Success(ast : de.fosd.typechef.parser.c.AST, remaining) => println(PrettyPrinter.print(ast))
      case parser.NoSuccess(msg, remaining, somethingElse) => println(msg)
    }
  }

  def parseAndPrintJava() = {
    import de.fosd.typechef.parser.java15.JavaParser
    import de.fosd.typechef.parser.java15.JavaLexer
    import de.fosd.typechef.featureexpr.FeatureExprFactory

    val parser = new JavaParser()
    val phrase = parser.phrase(parser.CompilationUnit)
    val parseResult = phrase(JavaLexer.lex("class A { public static int foo() { return 10; } }"), FeatureExprFactory.True)

    println(parseResult)
  }
}