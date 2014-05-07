package ru.spbau.preprocessing.lexer.lexemegraph;

public class Lexeme<TokenTypeBase> {
  private final TokenTypeBase myType;
  private final String myText;
  private final LexemeLocation myLocation;

  public Lexeme(TokenTypeBase type, String text, LexemeLocation location) {
    myType = type;
    myText = text;
    myLocation = location;
  }

  public TokenTypeBase getType() {
    return myType;
  }

  public String getText() {
    return myText;
  }

  public LexemeLocation getLocation() {
    return myLocation;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Lexeme lexeme = (Lexeme) o;
    return myLocation.equals(lexeme.myLocation) && myText.equals(lexeme.myText) && myType.equals(lexeme.myType);
  }

  @Override
  public int hashCode() {
    int result = myType.hashCode();
    result = 31 * result + myText.hashCode();
    result = 31 * result + myLocation.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "Lexeme: " + String.valueOf(myType);
  }
}
