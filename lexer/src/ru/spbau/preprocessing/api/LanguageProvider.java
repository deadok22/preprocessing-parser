package ru.spbau.preprocessing.api;

import ru.spbau.preprocessing.api.conditions.PresenceConditionFactory;
import ru.spbau.preprocessing.api.macros.MacroCallParser;
import ru.spbau.preprocessing.api.preprocessor.PreprocessorLanguageParser;

import java.util.Collection;

public interface LanguageProvider<TokenTypeBase> {
  LanguageLexer<TokenTypeBase> createLanguageLexer();
  PreprocessorLanguageParser createPreprocessorLanguageParser();
  PresenceConditionFactory createPresenceConditionFactory();
  Collection<MacroCallParser<TokenTypeBase>> createMacroCallParsers();
}
