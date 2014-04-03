package ru.spbau.preprocessing.api.macros;

import ru.spbau.preprocessing.lexer.lexemegraph.Lexeme;

public interface MacroCallParser<TokenTypeBase> {
  void reset();
  void consumeLexeme(Lexeme<TokenTypeBase> lexeme);
  MacroCallParserState getState();
  MacroCall<TokenTypeBase> getParsedCall();
}
