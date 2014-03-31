package ru.spbau.preprocessing.erlang;

import ru.spbau.preprocessing.api.LanguageLexer;
import ru.spbau.preprocessing.api.LanguageProvider;
import ru.spbau.preprocessing.api.conditions.PresenceConditionFactory;
import ru.spbau.preprocessing.api.preprocessor.PreprocessorLanguageParser;
import ru.spbau.preprocessing.erlang.preprocessor.ErlangPreprocessorLanguageParser;

import java.io.Reader;

public class ErlangLanguageProvider implements LanguageProvider<ErlangToken> {
  @Override
  public LanguageLexer<ErlangToken> createLanguageLexer() {
    return new ErlangLexer((Reader) null);
  }

  @Override
  public PreprocessorLanguageParser createPreprocessorLanguageParser() {
    return new ErlangPreprocessorLanguageParser();
  }

  @Override
  public PresenceConditionFactory createPresenceConditionFactory() {
    //TODO implement me
    throw new UnsupportedOperationException("Not implemented");
  }
}
