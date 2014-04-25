package ru.spbau.preprocessing.parser.earley.grammar;

import com.google.common.base.Joiner;

import java.util.Collections;
import java.util.List;

public class EarleyProduction {
  private final EarleyNonTerminal myLeft;
  private final List<EarleySymbol> myRight;

  EarleyProduction(EarleyNonTerminal left, List<EarleySymbol> right) {
    myLeft = left;
    myRight = Collections.unmodifiableList(right);
  }

  public EarleyNonTerminal getLeft() {
    return myLeft;
  }

  public List<EarleySymbol> getRight() {
    return myRight;
  }

  public String getName() {
    return myLeft.getName();
  }

  public boolean isLeftAssociative() {
    //TODO provide options for associativity of rules
    return true;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    EarleyProduction that = (EarleyProduction) o;
    return myLeft.equals(that.myLeft) && myRight.equals(that.myRight);
  }

  @Override
  public int hashCode() {
    int result = myLeft.hashCode();
    result = 31 * result + myRight.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return myLeft.toString() + " ::= " + Joiner.on(' ').join(myRight);
  }
}
