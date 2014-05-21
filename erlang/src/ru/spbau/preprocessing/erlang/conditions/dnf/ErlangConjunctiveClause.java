package ru.spbau.preprocessing.erlang.conditions.dnf;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.*;

import java.util.*;

public class ErlangConjunctiveClause extends ErlangBooleanExpression {
  /**
   * Maps macro names occurring in this clause to their
   * presence with or without negations. (false for negations)
   */
  private final ImmutableMap<String, Boolean> myConjuncts;

  private ErlangConjunctiveClause(ImmutableMap<String, Boolean> conjuncts) {
    myConjuncts = conjuncts;
  }

  public ImmutableMap<String, Boolean> getConjuncts() {
    return myConjuncts;
  }

  @Override
  public ErlangBooleanExpression and(ErlangBooleanExpression ebe) {
    if (ebe instanceof ErlangBooleanConstant || ebe instanceof ErlangDisjunctiveNormalForm) {
      return ebe.and(this);
    }
    if (ebe instanceof ErlangConjunctiveClause) {
      return and(Arrays.asList(this, (ErlangConjunctiveClause) ebe));
    }
    throw new AssertionError("Unexpected erlang boolean expression type");
  }

  @Override
  public ErlangBooleanExpression or(ErlangBooleanExpression ebe) {
    if (ebe instanceof ErlangBooleanConstant || ebe instanceof ErlangDisjunctiveNormalForm) {
      return ebe.or(this);
    }
    if (ebe instanceof ErlangConjunctiveClause) {
      return or(Iterators.forArray(this, (ErlangConjunctiveClause) ebe));
    }
    throw new AssertionError("Unexpected erlang boolean expression type");
  }

  @Override
  public ErlangConjunctiveClause not() {
    ImmutableMap.Builder<String, Boolean> notBuilder = ImmutableMap.builder();
    for (Map.Entry<String, Boolean> conjunct : myConjuncts.entrySet()) {
      notBuilder.put(conjunct.getKey(), !conjunct.getValue());
    }
    return new ErlangConjunctiveClause(notBuilder.build());
  }

  @Override
  public String toString() {
    Collection<String> conjunctStrings = Collections2.transform(myConjuncts.entrySet(), new Function<Map.Entry<String, Boolean>, String>() {
      @Override
      public String apply(Map.Entry<String, Boolean> definedMacro) {
        return definedMacro.getValue() ? definedMacro.getKey() : ("!" + definedMacro.getKey());
      }
    });
    return "(" + Joiner.on(" & ").join(conjunctStrings) + ")";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ErlangConjunctiveClause that = (ErlangConjunctiveClause) o;
    return myConjuncts.equals(that.myConjuncts);
  }

  @Override
  public int hashCode() {
    return myConjuncts.hashCode();
  }

  public static ErlangBooleanExpression and(Iterable<ErlangConjunctiveClause> conjunctiveClauses) {
    int maxNewLength = 0;
    for (ErlangConjunctiveClause conjunctiveClause : conjunctiveClauses) {
      maxNewLength += conjunctiveClause.myConjuncts.size();
    }
    Map<String, Boolean> andPrototype = Maps.newHashMapWithExpectedSize(maxNewLength);
    for (ErlangConjunctiveClause clause : conjunctiveClauses) {
      for (Map.Entry<String, Boolean> conjunct : clause.myConjuncts.entrySet()) {
        String macroName = conjunct.getKey();
        Boolean isPositive = conjunct.getValue();
        Boolean presentIsPositive = andPrototype.get(macroName);
        if (presentIsPositive == null) {
          andPrototype.put(macroName, isPositive);
        }
        // x & !x detected
        else if (presentIsPositive.booleanValue() != isPositive.booleanValue()) {
          return ErlangBooleanConstant.FALSE;
        }
      }
    }
    return new ErlangConjunctiveClause(ImmutableMap.copyOf(andPrototype));
  }

  public static ErlangBooleanExpression or(Iterator<ErlangConjunctiveClause> conjunctiveClausesIterator) {
    HashSet<ErlangConjunctiveClause> conjunctiveClauses = Sets.newHashSet(conjunctiveClausesIterator);
    if (andNot(conjunctiveClauses) == ErlangBooleanConstant.FALSE) return ErlangBooleanConstant.TRUE;
    return conjunctiveClauses.size() == 1 ? conjunctiveClauses.iterator().next() :
            new ErlangDisjunctiveNormalForm(ImmutableList.copyOf(conjunctiveClauses));
  }

  public static ErlangBooleanExpression andNot(Collection<ErlangConjunctiveClause> conjunctiveClauses) {
    return and(Collections2.transform(conjunctiveClauses, new Function<ErlangConjunctiveClause, ErlangConjunctiveClause>() {
      @Override
      public ErlangConjunctiveClause apply(ErlangConjunctiveClause clause) {
        return clause.not();
      }
    }));
  }

  public static ErlangConjunctiveClause macroDefined(String macroName, boolean isPositive) {
    return new ErlangConjunctiveClause(ImmutableMap.of(macroName, isPositive ? Boolean.TRUE : Boolean.FALSE));
  }
}
