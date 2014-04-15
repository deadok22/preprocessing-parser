package ru.spbau.preprocessing.erlang.conditions.dnf;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

import java.util.Iterator;

public class ErlangDisjunctiveNormalForm extends ErlangBooleanExpression {
  private final ImmutableList<ErlangConjunctiveClause> myClauses;

  ErlangDisjunctiveNormalForm(ImmutableList<ErlangConjunctiveClause> clauses) {
    assert clauses.size() > 1;
    myClauses = clauses;
  }

  @Override
  public ErlangBooleanExpression and(ErlangBooleanExpression ebe) {
    if (ebe instanceof ErlangBooleanConstant) {
      return ebe.and(this);
    }
    //TODO provide an efficient impl.
    ErlangBooleanExpression andResult = ErlangBooleanConstant.FALSE;
    for (ErlangConjunctiveClause clause : myClauses) {
      ErlangBooleanExpression andWithClause = clause.and(ebe);
      if (andWithClause instanceof ErlangBooleanConstant) {
        if (ebe == ErlangBooleanConstant.TRUE) {
          return ErlangBooleanConstant.TRUE;
        }
      }
      else {
        andResult = andResult.or(andWithClause);
      }
    }
    return andResult;
  }

  @Override
  public ErlangBooleanExpression or(ErlangBooleanExpression ebe) {
    if (ebe instanceof ErlangBooleanConstant) {
      return ebe.or(this);
    }
    return ErlangConjunctiveClause.or(Lists.newArrayList(Iterators.concat(myClauses.iterator(), iterateClauses(ebe))));
  }

  @Override
  public ErlangBooleanExpression not() {
    return ErlangConjunctiveClause.andNot(myClauses);
  }

  @Override
  public String toString() {
    return "(" + Joiner.on(" | ").join(myClauses) + ")";
  }

  private static Iterator<ErlangConjunctiveClause> iterateClauses(ErlangBooleanExpression ebe) {
    if (ebe instanceof ErlangConjunctiveClause) {
      return Iterators.singletonIterator((ErlangConjunctiveClause) ebe);
    }
    if (ebe instanceof ErlangDisjunctiveNormalForm) {
      return ((ErlangDisjunctiveNormalForm) ebe).myClauses.iterator();
    }
    throw new AssertionError("Unexpected erlang boolean expression type");
  }
}
