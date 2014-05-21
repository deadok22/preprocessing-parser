package ru.spbau.preprocessing.typechef.erlang

import ru.spbau.preprocessing.lexer.lexemegraph._
import ru.spbau.preprocessing.erlang.ErlangToken
import ru.spbau.preprocessing.erlang.conditions.ErlangPresenceCondition
import collection.JavaConversions._

object LexemeSequence {
  def from(lexemeGraph: LexemeGraphNode): List[ErlangLexemeWrapper] = {
    val v = new SequenceBuildingVisitor
    lexemeGraph.accept(v)
    v.result
  }

  private class SequenceBuildingVisitor(var sequence: List[ErlangLexemeWrapper] = Nil) extends LexemeGraphVisitor {
    def result: List[ErlangLexemeWrapper] = sequence.reverse

    override def visitLangNode(langNode: LexemeGraphLangNode[_]): Unit = {
      val pc = langNode.getPresenceCondition.asInstanceOf[ErlangPresenceCondition]
      langNode.getLexemes.foreach {
        case l: Lexeme[ErlangToken] =>
          sequence = ErlangLexemeWrapper.create(l, pc) :: sequence
      }
    }

    override def visitForkNode(forkNode: LexemeGraphForkNode): Unit = {
      forkNode.getChildren.foreach(_.accept(this))
    }
  }

}
