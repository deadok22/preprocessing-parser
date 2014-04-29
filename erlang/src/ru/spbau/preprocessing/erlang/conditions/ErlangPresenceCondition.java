package ru.spbau.preprocessing.erlang.conditions;

import ru.spbau.preprocessing.api.conditions.PresenceCondition;
import ru.spbau.preprocessing.erlang.conditions.dnf.ErlangBooleanConstant;
import ru.spbau.preprocessing.erlang.conditions.dnf.ErlangBooleanExpression;
import ru.spbau.preprocessing.erlang.conditions.dnf.ErlangConjunctiveClause;

public class ErlangPresenceCondition implements PresenceCondition {
  static final ErlangPresenceCondition TRUE = new ErlangPresenceCondition(ErlangBooleanConstant.TRUE);
  static final ErlangPresenceCondition FALSE = new ErlangPresenceCondition(ErlangBooleanConstant.FALSE);

  private final ErlangBooleanExpression myExpression;

  private ErlangPresenceCondition(ErlangBooleanExpression expression) {
    myExpression = expression;
  }

  @Override
  public PresenceCondition and(PresenceCondition presenceCondition) {
    assert presenceCondition instanceof ErlangPresenceCondition;
    ErlangBooleanExpression andExpr = myExpression.and(((ErlangPresenceCondition) presenceCondition).myExpression);
    return fromExpression(andExpr);
  }

  @Override
  public PresenceCondition or(PresenceCondition presenceCondition) {
    assert presenceCondition instanceof ErlangPresenceCondition;
    ErlangBooleanExpression orExpr = myExpression.or(((ErlangPresenceCondition) presenceCondition).myExpression);
    return fromExpression(orExpr);
  }

  @Override
  public PresenceCondition not() {
    return fromExpression(myExpression.not());
  }

  @Override
  public Value value() {
    return myExpression.isFalse() ? Value.FALSE :
            myExpression.isTrue() ? Value.TRUE :
                    Value.VARIANCE;
  }

  @Override
  public String toString() {
    return myExpression.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ErlangPresenceCondition that = (ErlangPresenceCondition) o;
    return myExpression.equals(that.myExpression);
  }

  @Override
  public int hashCode() {
    return myExpression.hashCode();
  }

  static ErlangPresenceCondition macroDefined(String macroName, boolean positive) {
    return new ErlangPresenceCondition(ErlangConjunctiveClause.macroDefined(macroName, positive));
  }

  private static ErlangPresenceCondition fromExpression(ErlangBooleanExpression expression) {
    return expression == ErlangBooleanConstant.TRUE ? TRUE :
            expression == ErlangBooleanConstant.FALSE ? FALSE :
                    new ErlangPresenceCondition(expression);
  }
}
