package ru.spbau.preprocessing.parser.earley.parser;

import ru.spbau.preprocessing.lexer.lexemegraph.Lexeme;
import ru.spbau.preprocessing.parser.earley.grammar.EarleyTerminal;

final class EarleyTerminalMatch<TokenTypeBase> {
  private final EarleyTerminal<TokenTypeBase> myTerminal;
  private final Lexeme<TokenTypeBase> myLexeme;

  EarleyTerminalMatch(EarleyTerminal<TokenTypeBase> terminal, Lexeme<TokenTypeBase> lexeme) {
    myTerminal = terminal;
    myLexeme = lexeme;
  }

  public EarleyTerminal<TokenTypeBase> getTerminal() {
    return myTerminal;
  }

  public Lexeme<TokenTypeBase> getLexeme() {
    return myLexeme;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    EarleyTerminalMatch that = (EarleyTerminalMatch) o;
    return myLexeme.equals(that.myLexeme) && myTerminal.equals(that.myTerminal);
  }

  @Override
  public int hashCode() {
    int result = myTerminal.hashCode();
    result = 31 * result + myLexeme.hashCode();
    return result;
  }
}
