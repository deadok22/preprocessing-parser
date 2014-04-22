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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    EarleyTerminal that = (EarleyTerminal) o;
    return myType.equals(that.myType);
  }

  @Override
  public int hashCode() {
    return myType.hashCode();
  }
}
