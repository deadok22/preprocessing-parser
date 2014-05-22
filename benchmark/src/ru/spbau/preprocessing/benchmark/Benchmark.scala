package ru.spbau.preprocessing.benchmark

import java.io.File

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
      print(f.getPath)
      print("\t")
      sw.start()
      val parsed: Boolean = pr.parse(f)
      val parseTime: Long = sw.stop()
      System.gc()
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