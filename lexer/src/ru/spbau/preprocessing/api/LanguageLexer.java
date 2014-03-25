package ru.spbau.preprocessing.api;

import java.io.IOException;

/**
 * A lexer which is aware of both tokens of target language and tokens of it's preprocessor.
 */
public interface LanguageLexer<TokenTypeBase> {
  void start(CharSequence buffer, int startIdx, int endIdx);
  void advance() throws IOException;
  TokenTypeBase tokenType();
  int tokenStartOffset();
  int tokenEndOffset();
}
