package ru.spbau.preprocessing.api.macros;

import ru.spbau.preprocessing.lexer.lexemegraph.Lexeme;

import java.util.Iterator;

public interface MacroCallParser<TokenTypeBase> {
  boolean parse(Iterator<Lexeme<TokenTypeBase>> lexemeStream);
  MacroCall<TokenTypeBase> getParsedCall();
}
