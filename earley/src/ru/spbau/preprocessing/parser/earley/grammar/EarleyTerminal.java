package ru.spbau.preprocessing.parser.earley.grammar;

public class EarleyTerminal<TokenTypeBase> implements EarleySymbol {
  private final TokenTypeBase myType;

  public EarleyTerminal(TokenTypeBase type) {
    myType = type;
  }

  @Override
  public String getName() {
    return String.valueOf(myType);
  }

  @Override
  public boolean isTerminal() {
    return true;
  }

  public TokenTypeBase getType() {
    return myType;
  }
}
