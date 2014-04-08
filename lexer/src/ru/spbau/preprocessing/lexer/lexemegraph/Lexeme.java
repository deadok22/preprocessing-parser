package ru.spbau.preprocessing.lexer.lexemegraph;

public class Lexeme<TokenTypeBase> {
  private final TokenTypeBase myType;
  private final String myText;

  public Lexeme(TokenTypeBase type, String text) {
    myType = type;
    myText = text;
  }

  public TokenTypeBase getType() {
    return myType;
  }

  public String getText() {
    return myText;
  }

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

  @Override
  public String toString() {
    return "Lexeme: " + String.valueOf(myType);
  }
}
