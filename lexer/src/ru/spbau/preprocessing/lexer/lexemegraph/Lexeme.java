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
}
