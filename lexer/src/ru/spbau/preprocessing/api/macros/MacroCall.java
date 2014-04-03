package ru.spbau.preprocessing.api.macros;

import ru.spbau.preprocessing.lexer.lexemegraph.Lexeme;

import java.util.List;

public interface MacroCall<TokenTypeBase> {
  String getMacroName();
  int getArity();
  List<List<Lexeme<TokenTypeBase>>> getArgumentsList();
}
