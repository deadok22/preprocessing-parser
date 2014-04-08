package ru.spbau.preprocessing.erlang;

import ru.spbau.preprocessing.api.LanguageLexer;
import ru.spbau.preprocessing.api.LanguageProvider;
import ru.spbau.preprocessing.api.conditions.PresenceConditionFactory;
import ru.spbau.preprocessing.api.macros.MacroCallParser;
import ru.spbau.preprocessing.api.preprocessor.PreprocessorLanguageParser;
import ru.spbau.preprocessing.erlang.conditions.ErlangPresenceConditionFactory;
import ru.spbau.preprocessing.erlang.preprocessor.ErlangPreprocessorLanguageParser;
import ru.spbau.preprocessing.erlang.preprocessor.macros.ErlangMacroCallParser;

import java.io.Reader;
import java.util.Arrays;
import java.util.Collection;

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
    return Arrays.<MacroCallParser<ErlangToken>>asList(new ErlangMacroCallParser(false), new ErlangMacroCallParser(true));
  }
}
