package ru.spbau.preprocessing.typechef.erlang

import ru.spbau.preprocessing.lexer.lexemegraph.{LexemeLocation, Lexeme}
import ru.spbau.preprocessing.erlang.ErlangToken
import de.fosd.typechef.parser.{NoPosition, Position, AbstractToken}
import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureExpr}
import ru.spbau.preprocessing.erlang.conditions.ErlangPresenceCondition
import ru.spbau.preprocessing.erlang.conditions.dnf.{ErlangDisjunctiveNormalForm, ErlangConjunctiveClause, ErlangBooleanConstant, ErlangBooleanExpression}
import collection.JavaConversions._

class ErlangLexemeWrapper(val lexeme: Lexeme[ErlangToken], val feature: FeatureExpr, val position: Position) extends AbstractToken {
  override def getPosition: Position = position

  override def getText: String = lexeme.getText

  override def getFeature: FeatureExpr = feature

  override def toString: String = getText + " <" + feature.toTextExpr + "> @" + position

  def getType: ErlangToken = lexeme.getType
}

class ErlangLexemePosition(startOffset: Int, file: String) extends Position {
  def this(l: LexemeLocation) = this(l.getStartOffset, l.getSourceFile.getPath)

  override def getColumn: Int = 0

  override def getLine: Int = 0

  override def getFile: String = file
}

object ErlangLexemeWrapper {
  def EOF = new ErlangLexemeWrapper(new Lexeme[ErlangToken](null, "<EOF>", null), FeatureExprFactory.True, NoPosition)

  def create(lexeme: Lexeme[ErlangToken], pc: ErlangPresenceCondition) = {
    new ErlangLexemeWrapper(lexeme, createFeatureFor(pc.getExpression), new ErlangLexemePosition(lexeme.getLocation))
  }

  def createFeatureFor(e: ErlangBooleanExpression): FeatureExpr =
    e match {
      case c: ErlangBooleanConstant =>
        if (c.isTrue) FeatureExprFactory.True else FeatureExprFactory.False
      case cc: ErlangConjunctiveClause =>
        cc.getConjuncts.entrySet().foldLeft(FeatureExprFactory.True)(
          (f, entry) =>
            f.and(
              if (entry.getValue) FeatureExprFactory.createDefinedExternal(entry.getKey)
              else FeatureExprFactory.createDefinedExternal(entry.getKey).not()
            )
        )
      case dnf: ErlangDisjunctiveNormalForm =>
        dnf.getClauses.foldLeft(FeatureExprFactory.False) ((f, cc) => f.or(createFeatureFor(cc)))
    }
}
