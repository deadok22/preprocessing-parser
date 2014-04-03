package ru.spbau.preprocessing.erlang;

import ru.spbau.preprocessing.api.LanguageLexer;
import ru.spbau.preprocessing.api.LanguageProvider;
import ru.spbau.preprocessing.api.conditions.PresenceConditionFactory;
import ru.spbau.preprocessing.api.macros.MacroCallParser;
import ru.spbau.preprocessing.api.preprocessor.PreprocessorLanguageParser;
import ru.spbau.preprocessing.erlang.conditions.ErlangPresenceConditionFactory;
import ru.spbau.preprocessing.erlang.preprocessor.ErlangPreprocessorLanguageParser;

import java.io.Reader;
import java.util.Collection;
import java.util.Collections;

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
    return new ErlangPresenceConditionFactory();
  }

  @Override
  public Collection<MacroCallParser<ErlangToken>> createMacroCallParsers() {
    //TODO implement
    return Collections.emptyList();
  }
}
