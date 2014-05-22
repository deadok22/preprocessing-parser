package ru.spbau.preprocessing.benchmark

import java.io.File

trait ParserRunner {
  def parse(file: File): Boolean
}
