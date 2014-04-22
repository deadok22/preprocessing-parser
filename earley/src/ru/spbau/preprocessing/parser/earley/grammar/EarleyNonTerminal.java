package ru.spbau.preprocessing.parser.earley.grammar;

public class EarleyNonTerminal implements EarleySymbol {
  private final String myName;

  public EarleyNonTerminal(String name) {
    myName = name;
  }

  @Override
  public String getName() {
    return myName;
  }

  @Override
  public boolean isTerminal() {
    return true;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    EarleyNonTerminal that = (EarleyNonTerminal) o;
    return myName.equals(that.myName);
  }

  @Override
  public int hashCode() {
    return myName.hashCode();
  }

  @Override
  public String toString() {
    return '{' + getName() + '}';
  }
}
