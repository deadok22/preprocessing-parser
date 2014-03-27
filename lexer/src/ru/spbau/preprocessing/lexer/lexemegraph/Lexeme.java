package ru.spbau.preprocessing.lexer.lexemegraph;

public class Lexeme<TokenTypeBase> {
  private final TokenTypeBase myType;

  public Lexeme(TokenTypeBase type) {
    myType = type;
  }

  public TokenTypeBase getType() {
    return myType;
  }

  //TODO add methods to provide token text?

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Lexeme lexeme = (Lexeme) o;
    return myType.equals(lexeme.myType);
  }

  @Override
  public int hashCode() {
    return myType.hashCode();
  }
}
