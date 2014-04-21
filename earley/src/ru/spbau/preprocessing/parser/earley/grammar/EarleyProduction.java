package ru.spbau.preprocessing.parser.earley.grammar;

import java.util.Collections;
import java.util.List;

public class EarleyProduction {
  private final EarleySymbol myLeft;
  private final List<EarleySymbol> myRight;

  EarleyProduction(EarleySymbol left, List<EarleySymbol> right) {
    assert !left.isTerminal();
    myLeft = left;
    myRight = Collections.unmodifiableList(right);
  }

  public EarleySymbol getLeft() {
    return myLeft;
  }

  public List<EarleySymbol> getRight() {
    return myRight;
  }

  public String getName() {
    return myLeft.getName();
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
}
