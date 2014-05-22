package ru.spbau.preprocessing.benchmark

import java.io.File
import ru.spbau.preprocessing.erlang.files.ErlangFileSystemSourceFile
import ru.spbau.preprocessing.lexer.PreprocessingLexer
import ru.spbau.preprocessing.erlang.{ErlangLanguageProvider, ErlangToken}
import ru.spbau.preprocessing.lexer.lexemegraph.LexemeGraphNode

object Benchmark extends App {

  args.toList match {
    case ((runner@("earley" | "typechef")) :: paths) =>
      val files = paths.map(new File(_))
      files.foreach(f => if (!f.isFile || !f.canRead) {
        println("Can't read " + f)
        sys.exit(1)
      })
      runBench(files, if (runner == "earley") EarleyParserRunner else TypeChefParserRunner)
    case _ => println("Usage: Benchmark <earley | typechef> file...")
  }

  def runBench(files: Iterable[java.io.File], pr: ParserRunner) {
    val sw = new StopWatch
    files.foreach(f => {
      val sourceFile: ErlangFileSystemSourceFile = new ErlangFileSystemSourceFile(f)
      val lexer: PreprocessingLexer[ErlangToken] = new PreprocessingLexer[ErlangToken](new ErlangLanguageProvider, sourceFile)
      val lexemeGraph: LexemeGraphNode = lexer.buildLexemeGraph
      val lexemes = pr.prepareLexemes(lexemeGraph)

      System.gc()

      sw.start()
      val parsed: Boolean = pr.parse(lexemes)
      val parseTime: Long = sw.stop()

      print(f.getPath)
      print("\t")
      print(if (parsed) "OK" else "FAIL")
      print("\t")
      println(parseTime)
    })
    println("TOTAL: " + sw.total + "ms")
  }
}

class StopWatch(private var myTotal: Long = 0L, private var myStarted: Long = 0L) {
  def start(): Unit = {
    myStarted = System.currentTimeMillis()
  }

  def stop(): Long = {
    val elapsed = System.currentTimeMillis() - myStarted
    myTotal += elapsed
    elapsed
  }

  def total: Long = myTotal
}