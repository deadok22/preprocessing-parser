package ru.spbau.preprocessing.typechef.erlang

import ru.spbau.preprocessing.lexer.lexemegraph.{LexemeLocation, Lexeme}
import ru.spbau.preprocessing.erlang.ErlangToken
import de.fosd.typechef.parser.{Position, AbstractToken}
import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureExpr}
import ru.spbau.preprocessing.erlang.conditions.ErlangPresenceCondition

class ErlangLexemeWrapper(val text: String, val feature: FeatureExpr, val position: Position) extends AbstractToken {
  override def getPosition: Position = position

  override def getText: String = text

  override def getFeature: FeatureExpr = feature

  override def toString: String = text + " <" + feature.toTextExpr + "> @" + position
}

class ErlangLexemePosition(startOffset: Int, file: String) extends Position {
  def this(l: LexemeLocation) = this(l.getStartOffset, l.getSourceFile.getPath)

  override def getColumn: Int = 0

  override def getLine: Int = 0

  override def getFile: String = file
}

object ErlangLexemeWrapper {
  def create(lexeme: Lexeme[ErlangToken], pc: ErlangPresenceCondition) = {
    new ErlangLexemeWrapper(lexeme.getText, createFeatureFor(pc), new ErlangLexemePosition(lexeme.getLocation))
  }

  def createFeatureFor(pc: ErlangPresenceCondition) : FeatureExpr = {
    //TODO construct a feature expression from erlang boolean expr
    FeatureExprFactory.True
  }
}
