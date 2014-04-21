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
}
